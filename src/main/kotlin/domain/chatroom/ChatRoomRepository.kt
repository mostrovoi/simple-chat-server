package com.mostro.ircchat.domain.chatroom

import com.mostro.ircchat.domain.user.ChatUser

interface ChatRoomRepository {
    fun findBy(chatRoomName: String): ChatRoom?
    fun removeUserFromChatRooms(chatUser: ChatUser)
    fun existsChatRoom(chatRoomName: String): Boolean
    fun createChatRoom(chatRoomName: String): ChatRoom
    fun findFirstChatRoomFor(chatUser: ChatUser): String?
    fun addUser(chatRoom: ChatRoom, chatUser: ChatUser): ChatRoom
    fun addMessage(chatRoom: ChatRoom, message: String): ChatRoom
    fun removeUserFromChatRoom(chatUser: ChatUser, chatRoom: ChatRoom): ChatRoom
}