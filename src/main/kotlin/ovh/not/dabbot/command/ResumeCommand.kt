package ovh.not.dabbot.command

import ovh.not.dabbot.Command

class ResumeCommand: Command("resume", "r", "unpause", "start", "continue", "unhalt") {
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
            ctx.server.queue!!.current { song ->
                if (song != null) {
                    if (!ctx.server.connected) {
                        ctx.server.open(ctx.getUserVoiceChannel()!!)
                    }
                    ctx.server.play(song)
                    ctx.reply("Resumed playing!")
                } else {
                    ctx.server.queue!!.next { song ->
                        if (song == null) {
                            ctx.reply("No song to resume!")
                        } else {
                            if (!ctx.server.connected) {
                                ctx.server.open(ctx.getUserVoiceChannel()!!)
                            }
                            ctx.server.play(song)
                            ctx.reply("Started playing the next song!")
                        }
                    }
                }
            }
        }
    }
}