package com.mostro.ircchat.domain.chatroom

import com.mostro.ircchat.domain.user.ChatUser
import java.util.*
import kotlin.collections.ArrayDeque

data class ChatRoom(val name: String, val messages: List<String> = emptyList(), val activeChatUsers: Set<ChatUser> = emptySet()) {

    private val MAX_USERS = 10
    private val MAX_SIZE_MESSAGES = 100

    fun isRoomFull() : Boolean {
        return this.activeChatUsers.size >= MAX_USERS
    }

    fun isHistoryFull() : Boolean {
        return this.messages.size >= MAX_SIZE_MESSAGES
    }

    fun isUserInChannel(chatUser: ChatUser): Boolean {
        return activeChatUsers.contains(chatUser)
    }
}