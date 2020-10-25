package com.mostro.ircchat

import com.mostro.ircchat.infrastructure.NettyChatServer

fun main() {
    val chatServer = NettyChatServer(8080)
    chatServer.start()
}
