package infrastructure

import com.mostro.ircchat.domain.chatroom.ChatRoom
import com.mostro.ircchat.domain.connection.ConnectionId
import com.mostro.ircchat.domain.messagesender.ChatMessageSender

class DummyChatMessageSender : ChatMessageSender {
    override fun sendMsgToAllChatUsers(message: String) {
        println("DEBUG ---> $message")
    }

    override fun sendMsgToChatRoom(message: String, chatRoom: ChatRoom) {
        println("DEBUG ---> $message")
    }

    override fun sendMsgToCurrentUser(connectionId: ConnectionId, message: String) {
        println("DEBUG ---> $message")
    }

    override fun sendHistoryMsgToCurrentUser(connectionId: ConnectionId, message: String) {
        println("DEBUG ---> $message")
    }
}
