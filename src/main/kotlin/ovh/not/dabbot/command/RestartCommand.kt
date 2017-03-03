package ovh.not.dabbot.command

import ovh.not.dabbot.Command

class RestartCommand: Command("restart") {
    override fun on(ctx: Context) {
        if (!ctx.server.connected || !ctx.server.playing) {
            ctx.reply("No music is playing!")
            return
        }
        ctx.server.audioPlayer.playingTrack.position = 0
        ctx.reply("Restarted track!")
    }
}