package org.dabbot.discord.command

import org.dabbot.discord.Command
import org.dabbot.discord.Permission

class RadioCommand: Command(Permission.RADIO, "radio", "station", "stations", "rdio", "statins") {
    override fun on(ctx: Context) {
        throw UnsupportedOperationException()
    }
}