package org.dabbot.discord.command

import org.dabbot.discord.Command
import org.dabbot.discord.Permission

class StopCommand: Command(Permission.STOP, "stop", "end", "disconnect", "close", "dc", "leave") {
    override fun on(ctx: Context) {
        if (!ctx.server.connected || !ctx.server.playing) {
            ctx.reply("No music is playing in this guild!")
            return
        }
        ctx.server.stop()
        ctx.server.close()
        ctx.reply("Music stopped! :warning: **THIS NO LONGER CLEARS THE SONG QUEUE!** Use `!!!clear` to do this.")
    }
}
