package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.dabbot.discord.Command
import org.dabbot.discord.Permission

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class SkipCommand: Command(Permission.SKIP, "skip", "s", "next", "n", "sk") {
    override fun on(ctx: Context) {
        if (!ctx.isUserInVoiceChannel()) {
            ctx.reply("You must be in a voice channel!")
            return
        }
        launch(CommonPool) {
            if (!ctx.server.connected) {
                ctx.server.open(ctx.getUserVoiceChannel()!!)
            }
            val song = ctx.server.queue!!.next()
            if (song == null) {
                ctx.reply("The song queue is empty!")
            } else {
                if (ctx.server.isPaused()) {
                    ctx.server.resume()
                    ctx.reply("Music was automatically resumed from being paused! To resume it manually, use `!!!resume`.")
                }
                ctx.server.play(song)
            }
        }
    }
}