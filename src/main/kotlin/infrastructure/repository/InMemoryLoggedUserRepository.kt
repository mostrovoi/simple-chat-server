package com.mostro.ircchat.infrastructure.repository

import com.mostro.ircchat.domain.connection.Connection
import com.mostro.ircchat.domain.connection.ConnectionId
import com.mostro.ircchat.domain.connection.ConnectionNotFoundException
import com.mostro.ircchat.domain.connection.LoggedUserRepository
import java.util.concurrent.ConcurrentHashMap

class InMemoryLoggedUserRepository : LoggedUserRepository {

    private val connections = ConcurrentHashMap<ConnectionId, Connection>()
    override fun findBy(connectionId: ConnectionId): Connection? {
        return connections[connectionId]
    }

    override fun isLoggedIn(connectionId: ConnectionId): Boolean {
        return this.findBy(connectionId) != null
    }

    @Synchronized
    override fun removeConnection(connection: Connection): Boolean {
        return connections.remove(connection.connectionId) != null
    }

    @Synchronized
    override fun setActiveRoom(connection: Connection, chatRoomName: String?) {
        val newConnection = Connection(connectionId = connection.connectionId, chatUser = connection.chatUser, activeChatRoom = chatRoomName)
        connections.replace(connection.connectionId, newConnection )
    }

    @Synchronized
    override fun addConnection(connection: Connection) {
        connections.put(connection.connectionId, connection)
    }

    override fun getOrFail(connectionId: ConnectionId) : Connection{
        return this.findBy(connectionId) ?: throw ConnectionNotFoundException()
    }


}