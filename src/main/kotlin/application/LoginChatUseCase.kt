package com.mostro.ircchat.application

import com.mostro.ircchat.domain.connection.Connection
import com.mostro.ircchat.domain.connection.ConnectionId
import com.mostro.ircchat.domain.connection.LoggedUserRepository
import com.mostro.ircchat.domain.messagesender.ChatMessageSender
import com.mostro.ircchat.domain.user.ChatUserRepository

class LoginChatUseCase(private val userRepository: ChatUserRepository, private val loggedUserRepository: LoggedUserRepository, private val chatMessageSender: ChatMessageSender) {

    fun execute(connectionId: ConnectionId, username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            chatMessageSender.sendMsgToCurrentUser(connectionId,"Empty username or password not allowed")
            return
        }
        if (loggedUserRepository.isLoggedIn(connectionId)) {
            chatMessageSender.sendMsgToCurrentUser(connectionId, "You are already logged in the system")
            return
        }
        if (!userRepository.existsUserName(username)) {
            userRepository.addUser(username, password)
            chatMessageSender.sendMsgToCurrentUser(connectionId, "User $username registered successfully")
        }

        val chatUser = userRepository.findBy(username)!!
        if (chatUser.password != password) {
            chatMessageSender.sendMsgToCurrentUser(connectionId, "The password provided for user $username is incorrect")
        } else {
            loggedUserRepository.addConnection(connection = Connection(connectionId = connectionId, chatUser = chatUser))
            chatMessageSender.sendMsgToCurrentUser(connectionId, "User $username logged in to the system")
        }
    }
}