package com.mostro.ircchat.domain.messagesender

import com.mostro.ircchat.domain.chatroom.ChatRoom
import com.mostro.ircchat.domain.chatroom.ChatRoomRepository
import com.mostro.ircchat.domain.connection.ConnectionId
import com.mostro.ircchat.domain.connection.LoggedUserRepository

interface ChatMessageSender {
    fun sendMsgToAllChatUsers(message: String)
    fun sendMsgToChatRoom(message: String, chatRoom: ChatRoom)
    fun sendMsgToCurrentUser(connectionId: ConnectionId,message: String)
    fun sendHistoryMsgToCurrentUser(connectionId: ConnectionId,message: String)
}
