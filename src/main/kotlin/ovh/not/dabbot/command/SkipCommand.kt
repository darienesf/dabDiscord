package ovh.not.dabbot.command

import ovh.not.dabbot.Command
import java.util.function.Consumer

class SkipCommand: Command("skip", "s", "next", "n") {
    override fun on(ctx: Context) {
        if (!ctx.isUserInVoiceChannel()) {
            ctx.reply("You must be in a voice channel!")
            return
        }
        if (!ctx.server.connected || !ctx.server.playing) {
            ctx.reply("No music is playing in this guild!")
            return
        }
        ctx.server.queue!!.next(Consumer { song ->
            ctx.server.play(song)
        })
    }
}