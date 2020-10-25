package com.mostro.ircchat.domain.connection

interface LoggedUserRepository {
     fun findBy(connectionId: ConnectionId): Connection?
     fun isLoggedIn(connectionId: ConnectionId): Boolean
     fun removeConnection(connection: Connection): Boolean
     fun setActiveRoom(connection: Connection, chatRoomName: String?)
     fun addConnection(connection: Connection)
     fun getOrFail(connectionId: ConnectionId) : Connection
}