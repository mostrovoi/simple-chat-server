package application

import com.mostro.ircchat.application.LoginChatUseCase
import com.mostro.ircchat.domain.connection.ConnectionId
import com.mostro.ircchat.domain.messagesender.ChatMessageSender
import domain.DummyChatRoomRepository
import domain.DummyChatUserRepository
import domain.DummyLoggedUserRepository
import infrastructure.DummyChatMessageSender
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class LoginChatUseCaseTest {

    @Test
    fun `should not create a user for empty username`() {
        val userRepository = DummyChatUserRepository()
        val loggedUserRepository = DummyLoggedUserRepository()

        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = DummyChatMessageSender()
        val usecase = LoginChatUseCase(userRepository = userRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)

        usecase.execute(ConnectionId("100"), "", "gamer")

        val existingUser = userRepository.findBy("")
        assertThat(existingUser).isEqualTo(null)
    }

    @Test
    fun `should not create a user for empty password`() {
        val userRepository = DummyChatUserRepository()
        val loggedUserRepository = DummyLoggedUserRepository()

        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = DummyChatMessageSender()
        val usecase = LoginChatUseCase(userRepository = userRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)

        usecase.execute(ConnectionId("100"), "bruno", "")

        val existingUser = userRepository.findBy("bruno")
        assertThat(existingUser).isEqualTo(null)
    }

    @Test
    fun `should create a new user and connection for incoming new user`() {
        val userRepository = DummyChatUserRepository()
        val loggedUserRepository = DummyLoggedUserRepository()

        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = DummyChatMessageSender()
        val usecase = LoginChatUseCase(userRepository = userRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)

        usecase.execute(ConnectionId("11"), "bruno", "gamer")

        val existingConnection = loggedUserRepository.findBy(ConnectionId("11"))
        val existingUser = userRepository.findBy("bruno")
        assertThat(existingUser!!.password).isEqualTo("gamer")
        assertThat(existingConnection!!.chatUser.username).isEqualTo("bruno")
    }

    @Test
    fun `should not create two users with same username`() {
        val userRepository = DummyChatUserRepository()
        val loggedUserRepository = DummyLoggedUserRepository()

        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = DummyChatMessageSender()
        val usecase = LoginChatUseCase(userRepository = userRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)

        usecase.execute(ConnectionId("11"), "bruno", "gamer")
        usecase.execute(ConnectionId("15"), "bruno", "fratini")

        val existingConnection = loggedUserRepository.findBy(ConnectionId("11"))
        val nonExistingConnection = loggedUserRepository.findBy(ConnectionId("15"))
        assertThat(existingConnection!!.chatUser.username).isEqualTo("bruno")
        assertThat(nonExistingConnection).isEqualTo(null)
    }
}