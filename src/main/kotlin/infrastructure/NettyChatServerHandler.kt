package com.mostro.ircchat.infrastructure

import com.mostro.ircchat.application.*
import com.mostro.ircchat.domain.connection.ConnectionId
import com.mostro.ircchat.domain.connection.ConnectionNotFoundException
import com.mostro.ircchat.infrastructure.commands.IRCCommands
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class NettyChatServerHandler(chatServer: NettyChatServer) : SimpleChannelInboundHandler<String>() {

    private val chatUserRepository = chatServer.chatUserRepository
    private val loggedUserRepository = chatServer.loggedUserRepository
    private val chatRoomRepository = chatServer.chatRoomRepository
    private val activeNettyChannelRepository = chatServer.activeNettyChannelRepository

    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.write(">>> IRCSERVER <<< Welcome\n")

        val welcomeMessage =
                        "  /login username pwd Logs in as authenticated user. A new user is created if not found any.\r\n" +
                        "  /join channel_name  Joins or creates and makes your active chat channel.\r\n" +
                        "  /quit channel_name  Quits channel_name if user is in it. If none is given user quits active channel\r\n" +
                        "  /users              Shows the users list of your currently active chat channel.\r\n" +
                        "  /exit               Disconnects from the server \r\n" +
                        "  /leave              Logs out from the server.\r\n\n"
        ctx.writeAndFlush(welcomeMessage)
        activeNettyChannelRepository.add(ctx.channel())
        println("New connection arrived to the server..: ${ctx.channel().id()}")
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        activeNettyChannelRepository.remove(ctx.channel())
        val chatMessageSender = NettyChatMessageSender(activeNettyChannelRepository = activeNettyChannelRepository, chatRoomRepository = chatRoomRepository, loggedUserRepository = loggedUserRepository)
        val leaveChatUseCase = LeaveChatUseCase(chatRoomRepository = chatRoomRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)
        val useCase = ExitChatUseCase(leaveChatUseCase = leaveChatUseCase, loggedUserRepository = loggedUserRepository)
        useCase.execute(connectionId = ConnectionId(ctx.channel().id().asLongText()))
        println("Connection removed from the server..: ${ctx.channel().id()}")
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: String) {
        val incomingChannel = ctx.channel()
        val connectionId = ConnectionId(incomingChannel.id().asLongText())
        val chatMessageSender = NettyChatMessageSender(activeNettyChannelRepository = activeNettyChannelRepository, chatRoomRepository = chatRoomRepository, loggedUserRepository = loggedUserRepository)
        try {
            require(msg.trim().isNotEmpty())
            val tokens = CommandParser.parseMessage(msg)

            when(tokens[0]) {
                IRCCommands.LOGIN.command -> {
                    require(tokens.size == 3)
                    val useCase = LoginChatUseCase(userRepository = chatUserRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)
                    useCase.execute(connectionId = connectionId, username = tokens[1], password = tokens[2])
                }
                IRCCommands.JOIN.command -> {
                    require(tokens.size == 2)
                    val useCase = JoinChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender )
                    useCase.execute(connectionId = connectionId, chatRoomName = tokens[1])
                }
                IRCCommands.LEAVE.command -> {
                    require(tokens.size == 1)
                    val useCase = LeaveChatUseCase(chatRoomRepository = chatRoomRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)
                    useCase.execute(connectionId = connectionId)
                }
                IRCCommands.QUIT.command -> {
                    require(tokens.size <= 2)
                    val useCase = QuitChatUseCase(chatRoomRepository =  chatRoomRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)
                    val chatroomName = if (tokens.size == 2) tokens[1] else null
                    useCase.execute(connectionId = connectionId, chatRoomName = chatroomName)
                }
                IRCCommands.USERS.command -> {
                    require(tokens.size == 1)
                    val useCase = ShowChatUsersUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
                    useCase.execute(connectionId = connectionId)
                }
                IRCCommands.EXIT.command -> {
                    require(tokens.size == 1)
                    val leaveChatUseCase = LeaveChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
                    val useCase = ExitChatUseCase(loggedUserRepository = loggedUserRepository, leaveChatUseCase = leaveChatUseCase)
                    useCase.execute(connectionId = connectionId)
                    ctx.writeAndFlush("Bye!").addListener(ChannelFutureListener.CLOSE)
                }
                else -> {
                    require(!tokens[0].startsWith("/"))
                    val useCase = SendMessageToChatRoomUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
                    useCase.execute(connectionId = connectionId, message = msg)
                }
            }
        } catch (exception: IllegalArgumentException) {
            chatMessageSender.sendMsgToCurrentUser(connectionId, "Invalid command")
        } catch (exception: ConnectionNotFoundException) {
            chatMessageSender.sendMsgToCurrentUser(connectionId, "You are not logged in to the system")
        }
    }
}