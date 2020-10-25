package com.mostro.ircchat.application

import com.mostro.ircchat.domain.chatroom.ChatRoomRepository
import com.mostro.ircchat.domain.connection.ConnectionId
import com.mostro.ircchat.domain.connection.LoggedUserRepository
import com.mostro.ircchat.domain.messagesender.ChatMessageSender
import com.mostro.ircchat.domain.messagesender.MessageEnricher
import java.time.LocalDateTime

class SendMessageToChatRoomUseCase(private val loggedUserRepository: LoggedUserRepository, private val chatRoomRepository: ChatRoomRepository, private val chatMessageSender: ChatMessageSender) {

    fun execute(connectionId: ConnectionId, message: String) {
        val connection = loggedUserRepository.getOrFail(connectionId)
        if(connection.activeChatRoom == null) {
            chatMessageSender.sendMsgToCurrentUser(connectionId, "You are not in any channel")
            return
        } else {
            val chatRoom = chatRoomRepository.findBy(connection.activeChatRoom)
            if (chatRoom != null) {
                val totalMessage = "${connection.chatUser.username} -> $message"
                val richMessage = MessageEnricher.getEnrichedMessage(totalMessage)
                val newChatRoom = chatRoomRepository.addMessage(chatRoom, richMessage)
                chatMessageSender.sendMsgToChatRoom(richMessage, newChatRoom)
            }
        }
    }

}
