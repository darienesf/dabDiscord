package ovh.not.dabbot.command

import ovh.not.dabbot.Command
import java.util.function.Consumer

class SkipCommand: Command("skip", "s", "next", "n") {
    override fun on(ctx: Context) {
        if (!ctx.isUserInVoiceChannel()) {
            ctx.reply("You must be in a voice channel!")
            return
        }
        if (!ctx.server.connected) {
            ctx.server.open(ctx.getUserVoiceChannel()!!)
        }
        ctx.server.queue!!.next(Consumer { song ->
            if (song == null) {
                ctx.reply("The song queue is empty!")
            } else {
                if (ctx.server.isPaused()) {
                    ctx.server.resume()
                    ctx.reply("Music was automatically resumed from being paused! To resume it manually, use `!!!resume`.")
                }
                ctx.server.play(song)
            }
        })
    }
}