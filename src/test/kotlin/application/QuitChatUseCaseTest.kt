package application

import com.mostro.ircchat.application.*
import com.mostro.ircchat.domain.connection.ConnectionId
import com.mostro.ircchat.domain.connection.ConnectionNotFoundException
import com.mostro.ircchat.domain.user.ChatUser
import domain.DummyChatRoomRepository
import domain.DummyChatUserRepository
import domain.DummyLoggedUserRepository
import infrastructure.DummyChatMessageSender
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class QuitChatUseCaseTest {

    @Test
    fun `should throw exception if user is not logged in`() {
        val loggedUserRepository = DummyLoggedUserRepository()
        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = DummyChatMessageSender()
        val quitChatUseCase = QuitChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)

        assertThrows(ConnectionNotFoundException::class.java) {
            quitChatUseCase.execute(ConnectionId("11"), "perro")
        }
    }

    @Test
    fun `should not quit from any channel if user is not in any channel`() {
        val userRepository = DummyChatUserRepository()
        val loggedUserRepository = DummyLoggedUserRepository()
        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = DummyChatMessageSender()
        val usecase = LoginChatUseCase(userRepository = userRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)
        usecase.execute(ConnectionId("11"), "bruno", "gamer")

        val quitChatUseCase = QuitChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        quitChatUseCase.execute(ConnectionId("11"), null)

        assertTrue(loggedUserRepository.isLoggedIn(ConnectionId("11")))
    }

    @Test
    fun `should quit from active channel and set the active connection to null`() {
        val userRepository = DummyChatUserRepository()
        val loggedUserRepository = DummyLoggedUserRepository()
        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = DummyChatMessageSender()
        val usecase = LoginChatUseCase(userRepository = userRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)
        usecase.execute(ConnectionId("11"), "bruno", "gamer")
        val joinUseCase = JoinChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        joinUseCase.execute(ConnectionId("11"), "pornoduro")

        val quitChatUseCase = QuitChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        quitChatUseCase.execute(ConnectionId("11"), null)

        val chatUser = ChatUser("bruno", "gamer")
        assertThat(chatRoomRepository.findBy("pornoduro")!!.activeChatUsers.contains(chatUser)).isEqualTo(false)
        assertThat(loggedUserRepository.findBy(ConnectionId("11"))!!.activeChatRoom).isEqualTo(null)
    }

    @Test
    fun `should quit from active channel if none is given`() {
        val userRepository = DummyChatUserRepository()
        val loggedUserRepository = DummyLoggedUserRepository()
        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = DummyChatMessageSender()
        val usecase = LoginChatUseCase(userRepository = userRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)
        usecase.execute(ConnectionId("11"), "bruno", "gamer")
        val joinUseCase = JoinChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        joinUseCase.execute(ConnectionId("11"), "pornoduro")
        joinUseCase.execute(ConnectionId("11"), "gamer")

        val quitChatUseCase = QuitChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        quitChatUseCase.execute(ConnectionId("11"), null)

        val chatUser = ChatUser("bruno", "gamer")
        assertThat(chatRoomRepository.findBy("pornoduro")!!.activeChatUsers.contains(chatUser)).isEqualTo(true)
        assertThat(chatRoomRepository.findBy("gamer")!!.activeChatUsers.contains(chatUser)).isEqualTo(false)
        assertThat(loggedUserRepository.findBy(ConnectionId("11"))!!.activeChatRoom).isEqualTo("pornoduro")
    }

    @Test
    fun `should not quit from active channel if a channel is given but does not exist`() {
        val userRepository = DummyChatUserRepository()
        val loggedUserRepository = DummyLoggedUserRepository()
        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = DummyChatMessageSender()
        val usecase = LoginChatUseCase(userRepository = userRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)
        usecase.execute(ConnectionId("11"), "bruno", "gamer")
        val joinUseCase = JoinChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        joinUseCase.execute(ConnectionId("11"), "pornoduro")

        val quitChatUseCase = QuitChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        quitChatUseCase.execute(ConnectionId("11"), "gamer")

        val chatUser = ChatUser("bruno", "gamer")
        assertThat(chatRoomRepository.findBy("pornoduro")!!.activeChatUsers.contains(chatUser)).isEqualTo(true)
        assertThat(loggedUserRepository.findBy(ConnectionId("11"))!!.activeChatRoom).isEqualTo("pornoduro")
    }

    @Test
    fun `should not quit from active channel if a channel is given but user is not in it`() {
        val userRepository = DummyChatUserRepository()
        val loggedUserRepository = DummyLoggedUserRepository()
        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = DummyChatMessageSender()
        val usecase = LoginChatUseCase(userRepository = userRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)
        usecase.execute(ConnectionId("11"), "bruno", "gamer")
        val joinUseCase = JoinChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        joinUseCase.execute(ConnectionId("11"), "pornoduro")
        chatRoomRepository.createChatRoom("gamer")

        val quitChatUseCase = QuitChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        quitChatUseCase.execute(ConnectionId("11"), "gamer")

        val chatUser = ChatUser("bruno", "gamer")
        assertThat(chatRoomRepository.findBy("pornoduro")!!.activeChatUsers.contains(chatUser)).isEqualTo(true)
        assertThat(loggedUserRepository.findBy(ConnectionId("11"))!!.activeChatRoom).isEqualTo("pornoduro")
    }

    @Test
    fun `should quit from given channel if exists and user is in it`() {
        val userRepository = DummyChatUserRepository()
        val loggedUserRepository = DummyLoggedUserRepository()
        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = DummyChatMessageSender()
        val usecase = LoginChatUseCase(userRepository = userRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)
        usecase.execute(ConnectionId("11"), "bruno", "gamer")
        val joinUseCase = JoinChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        joinUseCase.execute(ConnectionId("11"), "gamer")
        joinUseCase.execute(ConnectionId("11"), "pornoduro")
        joinUseCase.execute(ConnectionId("11"), "pililas")

        val quitChatUseCase = QuitChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        quitChatUseCase.execute(ConnectionId("11"), "pililas")

        val chatUser = ChatUser("bruno", "gamer")
        assertThat(chatRoomRepository.findBy("pornoduro")!!.activeChatUsers.contains(chatUser)).isEqualTo(true)
        assertThat(chatRoomRepository.findBy("gamer")!!.activeChatUsers.contains(chatUser)).isEqualTo(true)
        assertThat(chatRoomRepository.findBy("pililas")!!.activeChatUsers.contains(chatUser)).isEqualTo(false)

        assertThat(loggedUserRepository.findBy(ConnectionId("11"))!!.activeChatRoom).isEqualTo("gamer")
    }

    @Test
    fun `should quit from given channel but not active if exists and user is in it but not to join another channel`() {
        val userRepository = DummyChatUserRepository()
        val loggedUserRepository = DummyLoggedUserRepository()
        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = DummyChatMessageSender()
        val usecase = LoginChatUseCase(userRepository = userRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)
        usecase.execute(ConnectionId("11"), "bruno", "gamer")
        val joinUseCase = JoinChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        joinUseCase.execute(ConnectionId("11"), "gamer")
        joinUseCase.execute(ConnectionId("11"), "pornoduro")
        joinUseCase.execute(ConnectionId("11"), "pililas")

        val quitChatUseCase = QuitChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        quitChatUseCase.execute(ConnectionId("11"), "gamer")

        val chatUser = ChatUser("bruno", "gamer")
        assertThat(chatRoomRepository.findBy("pornoduro")!!.activeChatUsers.contains(chatUser)).isEqualTo(true)
        assertThat(chatRoomRepository.findBy("gamer")!!.activeChatUsers.contains(chatUser)).isEqualTo(false)
        assertThat(chatRoomRepository.findBy("pililas")!!.activeChatUsers.contains(chatUser)).isEqualTo(true)

        assertThat(loggedUserRepository.findBy(ConnectionId("11"))!!.activeChatRoom).isEqualTo("pililas")
    }
}