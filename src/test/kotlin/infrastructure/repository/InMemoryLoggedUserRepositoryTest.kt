package infrastructure.repository

import com.mostro.ircchat.domain.connection.Connection
import com.mostro.ircchat.domain.connection.ConnectionId
import com.mostro.ircchat.domain.user.ChatUser
import com.mostro.ircchat.infrastructure.repository.InMemoryLoggedUserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class InMemoryLoggedUserRepositoryTest {

    @Test
    fun `should add a new connection to the repository`() {
        val loggedUserRepository = InMemoryLoggedUserRepository()
        loggedUserRepository.addConnection(Connection(ConnectionId("11"), ChatUser("bruno", "gamer")))
        val insertedConnection = loggedUserRepository.findBy(ConnectionId("11"))
        assertThat(insertedConnection!!.chatUser.username).isEqualTo("bruno")
    }

    @Test
    fun `should remove a new connection to the repository`() {
        val loggedUserRepository = InMemoryLoggedUserRepository()
        val brunoConnection = Connection(ConnectionId("11"), ChatUser("bruno", "gamer"))

        loggedUserRepository.addConnection(brunoConnection)
        loggedUserRepository.findBy(ConnectionId("11"))
        val removedConnection = loggedUserRepository.removeConnection(brunoConnection)

        assertThat(removedConnection).isEqualTo(true)
    }
}