package com.mostro.ircchat.application

import com.mostro.ircchat.domain.chatroom.ChatRoomRepository
import com.mostro.ircchat.domain.connection.ConnectionId
import com.mostro.ircchat.domain.connection.LoggedUserRepository
import com.mostro.ircchat.domain.messagesender.ChatMessageSender
import com.mostro.ircchat.domain.messagesender.MessageEnricher
import java.time.LocalDateTime

class QuitChatUseCase(private val loggedUserRepository: LoggedUserRepository, private val chatRoomRepository: ChatRoomRepository, private val chatMessageSender: ChatMessageSender) {
    fun execute(connectionId: ConnectionId, chatRoomName: String?) {
        val connection = loggedUserRepository.getOrFail(connectionId)
        val chatRoomNameToLeave = chatRoomName ?: connection.activeChatRoom
        if(chatRoomNameToLeave == null) {
            chatMessageSender.sendMsgToCurrentUser(connectionId, "You are not in any channel at the moment")
            return
        }
        val leavingChatRoom = chatRoomRepository.findBy(chatRoomNameToLeave)

        if(leavingChatRoom!=null) {
            val message = "${connection.chatUser.username} has left this channel"
            val richMessage = MessageEnricher.getEnrichedMessage(message)
            val newChatRoom = chatRoomRepository.addMessage(leavingChatRoom, richMessage)
            chatMessageSender.sendMsgToChatRoom(richMessage, newChatRoom)
            chatRoomRepository.removeUserFromChatRoom(chatUser = connection.chatUser, chatRoom = newChatRoom)
        }

        val channelToJoin = chatRoomRepository.findFirstChatRoomFor(connection.chatUser)
        if (channelToJoin != null) {
            val joinUseCase = JoinChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
            joinUseCase.execute(connectionId = connectionId, chatRoomName = channelToJoin)
        }
        else {
            loggedUserRepository.setActiveRoom(connection,null)
        }
    }
}
