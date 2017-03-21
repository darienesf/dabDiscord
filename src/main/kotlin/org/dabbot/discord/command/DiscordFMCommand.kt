package org.dabbot.discord.command

import org.dabbot.discord.Command
import org.dabbot.discord.Permission

class DiscordFMCommand: Command(Permission.DISCORD_FM, "discordfm", "dfm") {
    override fun on(ctx: Context) {
        throw UnsupportedOperationException()
    }
}