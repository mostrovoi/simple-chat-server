package domain

import com.mostro.ircchat.domain.user.ChatUser
import com.mostro.ircchat.domain.user.ChatUserRepository
import java.util.concurrent.ConcurrentHashMap

class DummyChatUserRepository : ChatUserRepository{
    private val chatUsers = ConcurrentHashMap<String, ChatUser>()

    override fun findBy(username: String): ChatUser? {
        return chatUsers[username]
    }

    override fun existsUserName(username: String): Boolean {
        return this.findBy(username) != null
    }

    @Synchronized
    override fun addUser(username: String, password: String): ChatUser {
        val chatUser = ChatUser(username, password)
        chatUsers[username] = chatUser
        return chatUser
    }
}
