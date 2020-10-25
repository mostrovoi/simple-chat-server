package application

import com.mostro.ircchat.application.JoinChatUseCase
import com.mostro.ircchat.application.LoginChatUseCase
import com.mostro.ircchat.application.ShowChatUsersUseCase
import com.mostro.ircchat.domain.connection.ConnectionId
import com.mostro.ircchat.domain.connection.ConnectionNotFoundException
import domain.DummyChatRoomRepository
import domain.DummyChatUserRepository
import domain.DummyLoggedUserRepository
import infrastructure.CounterChatMessageSender
import infrastructure.DummyChatMessageSender
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ShowChatUsersUseCaseTest {

    @Test
    fun `should throw exception is user is not logged in`() {
        val loggedUserRepository = DummyLoggedUserRepository()
        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = DummyChatMessageSender()
        val connectionId = ConnectionId("100")

        val showUsersUseCase = ShowChatUsersUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)

        Assertions.assertThrows(ConnectionNotFoundException::class.java) {
            showUsersUseCase.execute(connectionId)
        }
    }

    @Test
    fun `should not show anything if user is not in any channel`() {
        val userRepository = DummyChatUserRepository()
        val loggedUserRepository = DummyLoggedUserRepository()
        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = CounterChatMessageSender()
        val connectionId = ConnectionId("100")
        val loginUseCase = LoginChatUseCase(userRepository = userRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)
        loginUseCase.execute(connectionId,"bruno","gamer")
        chatMessageSender.total = 0

        val showUsersUseCase = ShowChatUsersUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        showUsersUseCase.execute(connectionId)

        assertThat(chatMessageSender.total).isEqualTo(1)
    }


    @Test
    fun `should show 2 users if 2 users are in the channel`() {
        val userRepository = DummyChatUserRepository()
        val loggedUserRepository = DummyLoggedUserRepository()
        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = CounterChatMessageSender()
        val loginUseCase = LoginChatUseCase(userRepository = userRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)
        loginUseCase.execute(ConnectionId("100"),"bruno","gamer")
        loginUseCase.execute(ConnectionId("101"),"bruno1","gamer")
        val joinUseCase = JoinChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        joinUseCase.execute(ConnectionId("100"), "pornoduro")
        joinUseCase.execute(ConnectionId("101"), "pornoduro")
        chatMessageSender.total = 0

        val showUsersUseCase = ShowChatUsersUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        showUsersUseCase.execute(ConnectionId("101"))

        assertThat(chatMessageSender.total).isEqualTo(3)
    }
}