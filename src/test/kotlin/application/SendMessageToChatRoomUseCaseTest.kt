package application

import com.mostro.ircchat.application.JoinChatUseCase
import com.mostro.ircchat.application.LoginChatUseCase
import com.mostro.ircchat.application.SendMessageToChatRoomUseCase
import com.mostro.ircchat.domain.connection.ConnectionId
import com.mostro.ircchat.domain.connection.ConnectionNotFoundException
import domain.DummyChatRoomRepository
import domain.DummyChatUserRepository
import domain.DummyLoggedUserRepository
import infrastructure.CounterChatMessageSender
import infrastructure.DummyChatMessageSender
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

internal class SendMessageToChatRoomUseCaseTest {

    @Test
    fun `should throw exception if user is not logged in`() {
        val loggedUserRepository = DummyLoggedUserRepository()
        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = DummyChatMessageSender()

        val sendMessageToChatRoomUseCase = SendMessageToChatRoomUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)

        assertThrows(ConnectionNotFoundException::class.java) {
            sendMessageToChatRoomUseCase.execute(ConnectionId("100"), "pio")
        }
    }

    @Test
    fun `should not send message if user is not in the channel`() {
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

        val sendMessageToChatRoomUseCase = SendMessageToChatRoomUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        sendMessageToChatRoomUseCase.execute(ConnectionId("101"), "pio")

        Assertions.assertThat(chatMessageSender.total).isEqualTo(1)
    }
}