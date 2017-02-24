package ovh.not.dabbot.command

import ovh.not.dabbot.Command

class ClearCommand: Command("clear", "cl", "clean", "removeall", "empty") {
    override fun on(ctx: Context) {
        if (!ctx.server.connected || !ctx.server.playing) {
            ctx.reply("No music is playing in this guild!")
            return
        }
        ctx.server.queue!!.clear()
        ctx.reply("Queue cleared!")
    }
}
