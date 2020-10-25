package com.mostro.ircchat.domain.messagesender

import java.time.LocalDateTime

class MessageEnricher {

    companion object {
        fun getEnrichedMessage(message: String): String {
            return "[IRCServer] ${LocalDateTime.now()} - $message\r\n"
        }
    }
}