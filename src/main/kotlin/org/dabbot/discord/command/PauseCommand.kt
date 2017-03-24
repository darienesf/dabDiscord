package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.dabbot.discord.Command
import org.dabbot.discord.Permission

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class PauseCommand: Command(Permission.PAUSE, "pause", "halt", "break") {
    override fun on(ctx: Context) {
        if (!ctx.isUserInVoiceChannel()) {
            ctx.reply("Must be in a voice channel!")
            return
        }
        if (!ctx.server.playing) {
            ctx.reply("No music is playing!")
            return
        }
        if (ctx.server.isPaused()) {
            ctx.reply("Music is already paused! To resume playback it, use `!!!resume`.")
            return
        }
        if (!ctx.server.connected) {
            launch(CommonPool) {
                ctx.server.open(ctx.getUserVoiceChannel()!!)
            }
        }
        ctx.server.pause()
        ctx.reply("Music paused!")
    }
}