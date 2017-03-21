package org.dabbot.discord.command

import org.dabbot.discord.Command
import org.dabbot.discord.Permission

class ResumeCommand: Command(Permission.RESUME, "resume", "r", "unpause", "start", "continue", "unhalt", "resum") {
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
            val song = ctx.server.queue!!.current()
            if (song != null) {
                if (!ctx.server.connected) {
                    ctx.server.open(ctx.getUserVoiceChannel()!!)
                }
                ctx.server.play(song)
                ctx.reply("Resumed playing!")
            } else {
                val next = ctx.server.queue!!.next()
                if (next == null) {
                    ctx.reply("No song to resume!")
                } else {
                    if (!ctx.server.connected) {
                        ctx.server.open(ctx.getUserVoiceChannel()!!)
                    }
                    ctx.server.play(next)
                    ctx.reply("Started playing the next song!")
                }
            }
        }
    }
}