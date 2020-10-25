package infrastructure

import com.mostro.ircchat.domain.chatroom.ChatRoom
import com.mostro.ircchat.domain.connection.ConnectionId
import com.mostro.ircchat.domain.messagesender.ChatMessageSender

class CounterChatMessageSender : ChatMessageSender {
    var total = 0;
    override fun sendMsgToAllChatUsers(message: String) {
        total++;
    }

    override fun sendMsgToChatRoom(message: String, chatRoom: ChatRoom) {
        total++;
    }

    override fun sendMsgToCurrentUser(connectionId: ConnectionId, message: String) {
        total++;
    }

    override fun sendHistoryMsgToCurrentUser(connectionId: ConnectionId, message: String) {
        total++;
    }
}
