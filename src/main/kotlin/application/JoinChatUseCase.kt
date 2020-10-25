package com.mostro.ircchat.application

import com.mostro.ircchat.domain.chatroom.ChatRoomRepository
import com.mostro.ircchat.domain.connection.ConnectionId
import com.mostro.ircchat.domain.connection.LoggedUserRepository
import com.mostro.ircchat.domain.messagesender.ChatMessageSender
import com.mostro.ircchat.domain.messagesender.MessageEnricher
import java.time.LocalDateTime

class JoinChatUseCase(private val loggedUserRepository: LoggedUserRepository, private val chatRoomRepository: ChatRoomRepository, private val chatMessageSender: ChatMessageSender) {
    fun execute(connectionId: ConnectionId, chatRoomName: String) {
        val connection = loggedUserRepository.getOrFail(connectionId = connectionId)
        if(chatRoomName.trim().isEmpty()) {
            chatMessageSender.sendMsgToCurrentUser(connectionId,"You cannot join a channel with an empty name")
            return
        }
        var chatRoom = chatRoomRepository.findBy(chatRoomName = chatRoomName)
        if(chatRoom == null) {
            chatRoom = chatRoomRepository.createChatRoom(chatRoomName)
        }

        if(chatRoom.isUserInChannel(connection.chatUser)) {
            chatMessageSender.sendMsgToCurrentUser(connectionId,"You were already in channel ${chatRoomName}. Set as active channel")
            loggedUserRepository.setActiveRoom(connection, chatRoomName)
            chatRoomRepository.findBy(chatRoom.name)!!.messages.forEach { message ->
                chatMessageSender.sendHistoryMsgToCurrentUser(connectionId, message = message)
            }
            return
        }

        if(chatRoom.isRoomFull()) {
            chatMessageSender.sendMsgToCurrentUser(connectionId,"You cannot join the channel. Channel full")
            return
        }
        chatRoomRepository.findBy(chatRoom.name)!!.messages.forEach { message ->
            chatMessageSender.sendHistoryMsgToCurrentUser(connectionId, message = message)
        }
        loggedUserRepository.setActiveRoom(connection, chatRoomName)
        val message = "${connection.chatUser.username} has joined channel ${chatRoom.name}"
        val richMessage = MessageEnricher.getEnrichedMessage(message)
        val newChatRoom = chatRoomRepository.addMessage(chatRoom, richMessage)
        chatMessageSender.sendMsgToChatRoom(richMessage, newChatRoom)
        chatRoomRepository.addUser(chatRoom = newChatRoom, chatUser = connection.chatUser)

    }
}