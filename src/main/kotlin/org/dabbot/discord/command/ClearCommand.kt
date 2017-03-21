package org.dabbot.discord.command

import org.dabbot.discord.Command
import org.dabbot.discord.Permission

class ClearCommand: Command(Permission.CLEAR, "clear", "cl", "clean", "removeall", "empty") {
    override fun on(ctx: Context) {
        /*if (!ctx.server.connected || !ctx.server.playing) {
            ctx.reply("No music is playing in this guild!")
            return
        }*/
        ctx.server.queue!!.clear()
        ctx.reply("Queue cleared!")
    }
}
