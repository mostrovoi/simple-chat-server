package com.mostro.ircchat.infrastructure.repository

import com.mostro.ircchat.domain.chatroom.ChatRoom
import com.mostro.ircchat.domain.chatroom.ChatRoomRepository
import com.mostro.ircchat.domain.user.ChatUser
import java.util.concurrent.ConcurrentHashMap

class InMemoryChatRoomRepository : ChatRoomRepository {

    private val chatRooms = ConcurrentHashMap<String, ChatRoom>()

    override fun findBy(chatRoomName: String): ChatRoom? {
        return chatRooms[chatRoomName]
    }

    @Synchronized
    override fun removeUserFromChatRooms(chatUser: ChatUser) {
        for(chatRoom in chatRooms) {
           removeUser(chatRoom.value,chatUser)
        }
    }

    @Synchronized
    override fun removeUserFromChatRoom(chatUser: ChatUser, chatRoom: ChatRoom): ChatRoom {
        return removeUser(chatRoom, chatUser)
    }

    override fun existsChatRoom(chatRoomName: String): Boolean {
        return chatRooms.contains(chatRoomName)
    }

    @Synchronized
    override fun createChatRoom(chatRoomName: String): ChatRoom {
        val chatRoom = ChatRoom(name = chatRoomName)
        chatRooms[chatRoomName] = chatRoom
        return chatRoom
    }

    override fun findFirstChatRoomFor(chatUser: ChatUser): String? {
        for(chatRoom in chatRooms) {
            if(chatRoom.value.isUserInChannel(chatUser)) {
                return chatRoom.key
            }
        }
        return null
    }

    @Synchronized
    override fun addUser(chatRoom: ChatRoom, chatUser: ChatUser): ChatRoom {
        var newChatRoom = chatRoom
        if(!chatRoom.isRoomFull()) {
            val users = chatRoom.activeChatUsers
            val newUsers = users + chatUser
            newChatRoom = ChatRoom(chatRoom.name, chatRoom.messages, newUsers)
            chatRooms[chatRoom.name] = newChatRoom
            return newChatRoom
        }
        return newChatRoom
    }

    @Synchronized
    override fun addMessage(chatRoom: ChatRoom, message: String): ChatRoom {
        var messages = chatRoom.messages.toMutableList()
        if(chatRoom.isHistoryFull()) {
            messages.drop(1)
        }
        messages.add(message)
        val newChatRoom = ChatRoom(chatRoom.name, messages, chatRoom.activeChatUsers)
        chatRooms[chatRoom.name] = newChatRoom
        return newChatRoom
    }

    @Synchronized
    private fun removeUser(chatRoom: ChatRoom, chatUser: ChatUser): ChatRoom {
        val newUsers = chatRoom.activeChatUsers.filter {  cu -> cu != chatUser  }.toSet()
        val newChatRoom = ChatRoom(chatRoom.name, chatRoom.messages, newUsers)
        chatRooms[chatRoom.name] = newChatRoom
        return newChatRoom
    }
}