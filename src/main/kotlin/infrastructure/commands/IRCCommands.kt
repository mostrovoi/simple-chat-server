package com.mostro.ircchat.infrastructure.commands

enum class IRCCommands(val command: String) {
    LOGIN("/login"),
    JOIN("/join"),
    LEAVE("/leave"),
    QUIT("/quit"),
    USERS("/users"),
    EXIT("/exit")
}