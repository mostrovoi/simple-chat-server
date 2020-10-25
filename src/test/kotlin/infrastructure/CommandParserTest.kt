package infrastructure

import com.mostro.ircchat.infrastructure.CommandParser
import org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.Test

internal class CommandParserTest {

    @Test
    fun `return 3 parts when 3 spaces are found`() {
        val firstOption = "pe"
        val command = "/join"
        val commands = CommandParser.parseMessage("/join pe pe")
        assertEquals(3, commands.size)
        assertEquals(command, commands[0])
        assertEquals(firstOption, commands[1])
    }
}