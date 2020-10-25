package com.mostro.ircchat.infrastructure

import com.mostro.ircchat.domain.chatroom.ChatRoom
import com.mostro.ircchat.domain.chatroom.ChatRoomRepository
import com.mostro.ircchat.domain.connection.ConnectionId
import com.mostro.ircchat.domain.connection.LoggedUserRepository
import com.mostro.ircchat.domain.messagesender.ChatMessageSender
import com.mostro.ircchat.domain.messagesender.MessageEnricher
import io.netty.channel.group.DefaultChannelGroup
import java.time.LocalDateTime

class NettyChatMessageSender(private val activeNettyChannelRepository: DefaultChannelGroup, private val chatRoomRepository: ChatRoomRepository, private val loggedUserRepository: LoggedUserRepository) : ChatMessageSender {

    override fun sendMsgToAllChatUsers(message: String) {
        for(channel in activeNettyChannelRepository) {
            channel.writeAndFlush(message)
        }
    }

    override fun sendMsgToChatRoom(message: String, chatRoom: ChatRoom) {
        for(channel in activeNettyChannelRepository) {
            val currentConnection = loggedUserRepository.findBy(ConnectionId(channel.id().asLongText()))
            if (currentConnection!=null && currentConnection.activeChatRoom == chatRoom.name) {
                channel.writeAndFlush(message)
            }
        }
    }

    override fun sendMsgToCurrentUser(connectionId: ConnectionId, message: String) {
        val enrichedMessage = MessageEnricher.getEnrichedMessage(message)
        this.sendHistoryMsgToCurrentUser(connectionId, enrichedMessage)
    }

    override fun sendHistoryMsgToCurrentUser(connectionId: ConnectionId, message: String) {
        for(channel in activeNettyChannelRepository) {
            val newConnectionId = ConnectionId(channel.id().asLongText())
            if(newConnectionId == connectionId)
                channel.writeAndFlush("$message")
        }
    }

}
