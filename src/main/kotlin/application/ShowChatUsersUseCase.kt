package com.mostro.ircchat.application

import com.mostro.ircchat.domain.chatroom.ChatRoomRepository
import com.mostro.ircchat.domain.connection.ConnectionId
import com.mostro.ircchat.domain.connection.LoggedUserRepository
import com.mostro.ircchat.domain.messagesender.ChatMessageSender

class ShowChatUsersUseCase(private val loggedUserRepository: LoggedUserRepository, private val chatRoomRepository: ChatRoomRepository, private val chatMessageSender: ChatMessageSender) {

    fun execute(connectionId: ConnectionId) {
        val connection = loggedUserRepository.getOrFail(connectionId)
        if(connection.activeChatRoom==null) {
            chatMessageSender.sendMsgToCurrentUser(connectionId, "You are not in any chat room")
            return
        }
        val currentChatRoom = chatRoomRepository.findBy(connection.activeChatRoom)
        if(currentChatRoom == null) {
            chatMessageSender.sendMsgToCurrentUser(connectionId, "Unexpected error. You are not in any chat room")
            return
        }
        val users = currentChatRoom.activeChatUsers
        chatMessageSender.sendMsgToCurrentUser(connectionId, "List of users for chatRoom: ${connection.activeChatRoom} -> ")
        for(user in users) {
            chatMessageSender.sendMsgToCurrentUser(connectionId, " ${user.username}")
        }
    }
}
