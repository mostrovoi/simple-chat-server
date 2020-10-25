package com.mostro.ircchat.application

import com.mostro.ircchat.domain.chatroom.ChatRoomRepository
import com.mostro.ircchat.domain.connection.ConnectionId
import com.mostro.ircchat.domain.connection.LoggedUserRepository
import com.mostro.ircchat.domain.messagesender.ChatMessageSender
import com.mostro.ircchat.domain.messagesender.MessageEnricher

class LeaveChatUseCase(private val loggedUserRepository: LoggedUserRepository, private val chatRoomRepository: ChatRoomRepository, private val chatMessageSender: ChatMessageSender) {
    fun execute(connectionId : ConnectionId) {
        val connection = loggedUserRepository.getOrFail(connectionId)
        chatRoomRepository.removeUserFromChatRooms(chatUser = connection.chatUser)
        loggedUserRepository.removeConnection(connection = connection)
        chatMessageSender.sendMsgToAllChatUsers(MessageEnricher.getEnrichedMessage("${connection.chatUser.username} has left the system"))
        chatMessageSender.sendMsgToCurrentUser(connectionId,"You have been logged out from the server")
    }
}
