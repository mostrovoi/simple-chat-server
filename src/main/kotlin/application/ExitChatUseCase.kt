package com.mostro.ircchat.application

import com.mostro.ircchat.domain.connection.ConnectionId
import com.mostro.ircchat.domain.connection.LoggedUserRepository

class ExitChatUseCase(private val leaveChatUseCase: LeaveChatUseCase, private val loggedUserRepository: LoggedUserRepository) {

    fun execute(connectionId: ConnectionId) {
        val connection = loggedUserRepository.findBy(connectionId)
        if(connection != null) {
            leaveChatUseCase.execute(connection.connectionId)
        }
    }
}
