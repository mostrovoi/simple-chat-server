package application

import com.mostro.ircchat.application.*
import com.mostro.ircchat.domain.connection.ConnectionId
import com.mostro.ircchat.domain.user.ChatUser
import domain.DummyChatRoomRepository
import domain.DummyChatUserRepository
import domain.DummyLoggedUserRepository
import infrastructure.CounterChatMessageSender
import infrastructure.DummyChatMessageSender
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

internal class ExitChatUseCaseTest {

    @Test
    fun `should not throw exception if user is not logged in`() {
        val loggedUserRepository = DummyLoggedUserRepository()
        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = CounterChatMessageSender()

        val leaveChatUseCase = LeaveChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        val exitChatUseCase = ExitChatUseCase(leaveChatUseCase = leaveChatUseCase, loggedUserRepository = loggedUserRepository)
        exitChatUseCase.execute(ConnectionId("11"))

        assertFalse(loggedUserRepository.isLoggedIn(ConnectionId("11")))
    }

    @Test
    fun `should remove connection, remove user from chatrooms and from system if user was logged in and in chat rooms`() {
        val userRepository = DummyChatUserRepository()
        val loggedUserRepository = DummyLoggedUserRepository()
        val chatRoomRepository = DummyChatRoomRepository()
        val chatMessageSender = DummyChatMessageSender()
        val usecase = LoginChatUseCase(userRepository = userRepository, loggedUserRepository = loggedUserRepository, chatMessageSender = chatMessageSender)
        usecase.execute(ConnectionId("11"), "bruno", "gamer")
        val joinUseCase = JoinChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        joinUseCase.execute(ConnectionId("11"), "pornoduro")
        joinUseCase.execute(ConnectionId("11"), "pecadorr")

        val leaveChatUseCase = LeaveChatUseCase(loggedUserRepository = loggedUserRepository, chatRoomRepository = chatRoomRepository, chatMessageSender = chatMessageSender)
        val exitChatUseCase = ExitChatUseCase(leaveChatUseCase = leaveChatUseCase, loggedUserRepository = loggedUserRepository)
        exitChatUseCase.execute(ConnectionId("11"))

        val chatUser = ChatUser("bruno","gamer")
        assertFalse(chatRoomRepository.findBy("pornoduro")!!.activeChatUsers.contains(chatUser))
        assertFalse(chatRoomRepository.findBy("pecadorr")!!.activeChatUsers.contains(chatUser))
        assertFalse(loggedUserRepository.isLoggedIn(ConnectionId("11")))
    }
}