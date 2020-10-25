package application

import com.mostro.ircchat.application.JoinChatUseCase
import com.mostro.ircchat.application.LoginChatUseCase
import com.mostro.ircchat.domain.connection.ConnectionId
import com.mostro.ircchat.domain.connection.ConnectionNotFoundException
import com.mostro.ircchat.domain.user.ChatUser
import domain.DummyChatRoomRepository
import domain.DummyChatUserRepository
import domain.DummyLoggedUserRepository
import infrastructure.DummyChatMessageSender
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class JoinChatUseCaseTest {

    @Test
    fun `should join the channel and create the channel if the user has previously logged in and the channel did not exist`() {
        val userRepository = DummyChatUserRepository()
        val loggedUserRepository = DummyLoggedUserRepository()

        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = DummyChatMessageSender()
        val connectionId = ConnectionId("100")
        val loginUseCase = LoginChatUseCase(userRepository = userRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)
        loginUseCase.execute(connectionId,"bruno","gamer")

        val useCase = JoinChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        useCase.execute(ConnectionId("100"), "pornoduro")

        assertThat(loggedUserRepository.findBy(ConnectionId("100"))!!.activeChatRoom).isEqualTo("pornoduro")
        val chatRoom = chatRoomRepository.findBy("pornoduro")
        assertThat(chatRoom!!.name).isEqualTo("pornoduro")
        assertThat(chatRoom!!.messages.size).isEqualTo(1)
        assertThat(chatRoom!!.isRoomFull()).isEqualTo(false)
        assertTrue(chatRoom!!.activeChatUsers.contains(ChatUser("bruno","gamer")))
        assertThat(chatRoom!!.activeChatUsers.size).isEqualTo(1)
    }

    @Test
    fun `should set activeChat room to the latest joined channel`() {
        val userRepository = DummyChatUserRepository()
        val loggedUserRepository = DummyLoggedUserRepository()

        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = DummyChatMessageSender()
        val connectionId = ConnectionId("100")
        val loginUseCase = LoginChatUseCase(userRepository = userRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)
        loginUseCase.execute(connectionId,"bruno","gamer")
        chatRoomRepository.createChatRoom("pornoduro")

        val useCase = JoinChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        useCase.execute(ConnectionId("100"), "pornoduro")
        assertThat(loggedUserRepository.findBy(ConnectionId("100"))!!.activeChatRoom).isEqualTo("pornoduro")
        useCase.execute(ConnectionId("100"), "gamer")
        assertThat(loggedUserRepository.findBy(ConnectionId("100"))!!.activeChatRoom).isEqualTo("gamer")
    }

    @Test
    fun `should join the channel if the user has previously logged in and the channel existed`() {
        val userRepository = DummyChatUserRepository()
        val loggedUserRepository = DummyLoggedUserRepository()

        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = DummyChatMessageSender()
        val connectionId = ConnectionId("100")
        val loginUseCase = LoginChatUseCase(userRepository = userRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)
        loginUseCase.execute(connectionId,"bruno","gamer")
        chatRoomRepository.createChatRoom("pornoduro")

        val useCase = JoinChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        useCase.execute(ConnectionId("100"), "pornoduro")

        assertThat(loggedUserRepository.findBy(ConnectionId("100"))!!.activeChatRoom).isEqualTo("pornoduro")
        val chatRoom = chatRoomRepository.findBy("pornoduro")
        assertThat(chatRoom!!.name).isEqualTo("pornoduro")
        assertTrue(chatRoom!!.activeChatUsers.contains(ChatUser("bruno","gamer")))
        assertThat(chatRoom!!.activeChatUsers.size).isEqualTo(1)
    }

    @Test
    fun `should not allow to join the channel and not create it if chatRoomName is empty`() {
        val userRepository = DummyChatUserRepository()
        val loggedUserRepository = DummyLoggedUserRepository()

        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = DummyChatMessageSender()
        val connectionId = ConnectionId("100")
        val loginUseCase = LoginChatUseCase(userRepository = userRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)
        loginUseCase.execute(connectionId,"bruno","gamer")

        val useCase = JoinChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        useCase.execute(ConnectionId("100"), "")

        assertThat(loggedUserRepository.findBy(ConnectionId("100"))!!.activeChatRoom).isEqualTo(null)
        val chatRoom = chatRoomRepository.findBy("pornoduro")
        assertThat(chatRoom).isEqualTo(null)
    }

    @Test
    fun `should not join the channel if the user is not logged in`() {
        val loggedUserRepository = DummyLoggedUserRepository()
        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = DummyChatMessageSender()

        val useCase = JoinChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        Assertions.assertThrows(ConnectionNotFoundException::class.java) {
            useCase.execute(ConnectionId("100"), "pornoduro")
        }
    }

    @Test
    fun `should not join the channel if the user is already in the channel`() {
        val userRepository = DummyChatUserRepository()
        val loggedUserRepository = DummyLoggedUserRepository()
        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = DummyChatMessageSender()
        val connectionId = ConnectionId("100")
        val loginUseCase = LoginChatUseCase(userRepository = userRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)
        loginUseCase.execute(connectionId,"bruno","gamer")

        val joinUseCase = JoinChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        joinUseCase.execute(ConnectionId("100"), "pornoduro")
        joinUseCase.execute(ConnectionId("100"), "pornoduro")
        val chatRoom = chatRoomRepository.findBy("pornoduro")
        assertThat(chatRoom!!.activeChatUsers.size).isEqualTo(1)
    }

    @Test
    fun `should not join the channel if the channel is full`() {
        val userRepository = DummyChatUserRepository()
        val loggedUserRepository = DummyLoggedUserRepository()
        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = DummyChatMessageSender()

        val loginUseCase = LoginChatUseCase(userRepository = userRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)
        for(numUser in 1..100) {
            val connectionId = ConnectionId("10$numUser")
            loginUseCase.execute(connectionId, "bruno$numUser", "gamer")
        }

        val joinUseCase = JoinChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        for(numUser in 1..100) {
            val connectionId = ConnectionId("10$numUser")
            joinUseCase.execute(connectionId, "pornoduro")
        }

        val chatRoom = chatRoomRepository.findBy("pornoduro")
        assertTrue(chatRoom!!.isRoomFull())
        assertThat(chatRoom.activeChatUsers.contains(ChatUser("bruno1","gamer"))).isEqualTo(true)
        assertThat(chatRoom.activeChatUsers.contains(ChatUser("bruno10","gamer"))).isEqualTo(true)
        assertThat(chatRoom.activeChatUsers.contains(ChatUser("bruno11","gamer"))).isEqualTo(false)

    }


}