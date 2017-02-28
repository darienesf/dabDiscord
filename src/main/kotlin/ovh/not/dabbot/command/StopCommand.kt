package ovh.not.dabbot.command

import ovh.not.dabbot.Command

class StopCommand: Command("stop", "end", "disconnect", "close", "dc", "leave") {
    override fun on(ctx: Context) {
        if (!ctx.server.connected || !ctx.server.playing) {
            ctx.reply("No music is playing in this guild!")
            return
        }
        ctx.server.stop()
        ctx.reply("Music stopped! :warning: **THIS NO LONGER CLEARS THE SONG QUEUE!** Use `!!!clear` to do this.")
    }
}
