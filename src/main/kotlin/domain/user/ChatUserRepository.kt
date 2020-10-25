package com.mostro.ircchat.domain.user

interface ChatUserRepository {
    fun findBy(username: String): ChatUser?
    fun existsUserName(username: String): Boolean
    fun addUser(username: String, password: String): ChatUser
}