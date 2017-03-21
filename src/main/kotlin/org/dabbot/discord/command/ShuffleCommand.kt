package org.dabbot.discord.command

import org.dabbot.discord.Command
import org.dabbot.discord.Permission

class ShuffleCommand: Command(Permission.SHUFFLE, "shuffle", "shufle", "mix", "shuffl") {
    override fun on(ctx: Context) {
        ctx.server.queue!!.shuffle()
        ctx.reply("Queue shuffled!")
    }
}
