package com.mostro.ircchat.domain.connection

import com.mostro.ircchat.domain.user.ChatUser

data class Connection(val connectionId: ConnectionId, val chatUser: ChatUser,val activeChatRoom: String? = null)