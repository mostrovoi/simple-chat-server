package com.mostro.ircchat.infrastructure

class CommandParser {
    companion object {
        fun parseMessage(message: String): List<String> {
            return message.trim().split(" ")
        }
    }
}