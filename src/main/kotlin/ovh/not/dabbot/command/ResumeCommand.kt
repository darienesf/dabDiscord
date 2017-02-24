package ovh.not.dabbot.command

import ovh.not.dabbot.Command
import java.util.function.Consumer

class ResumeCommand: Command("resume", "r", "unpause", "start", "continue") {
    override fun on(ctx: Context) {
        if (!ctx.isUserInVoiceChannel()) {
            ctx.reply("You must be in a voice channel!")
            return
        }
        if (ctx.server.playing) {
            if (!ctx.server.isPaused()) {
                ctx.reply("Music is not paused!")
                return
            }
            if (!ctx.server.connected) {
                ctx.server.open(ctx.getUserVoiceChannel()!!)
            }
            ctx.server.resume()
            ctx.reply("Music resumed!")
        } else {
            ctx.server.queue!!.current(Consumer { song ->
                if (song != null) {
                    if (!ctx.server.connected) {
                        ctx.server.open(ctx.getUserVoiceChannel()!!)
                    }
                    ctx.server.play(song)
                    ctx.reply("Resumed playing!")
                } else {
                    ctx.reply("No song to resume!")
                }
            })
        }
    }
}