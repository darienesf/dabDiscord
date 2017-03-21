package org.dabbot.discord.command

import org.dabbot.discord.Command
import org.dabbot.discord.Permission

class RestartCommand: Command(Permission.RESTART, "restart") {
    override fun on(ctx: Context) {
        if (!ctx.server.connected || !ctx.server.playing) {
            ctx.reply("No music is playing!")
            return
        }
        ctx.server.audioPlayer.playingTrack.position = 0
        ctx.reply("Restarted track!")
    }
}