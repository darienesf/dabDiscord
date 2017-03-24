package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.dabbot.discord.Command
import org.dabbot.discord.Permission

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class StopCommand: Command(Permission.STOP, "stop", "end", "disconnect", "close", "dc", "leave") {
    override fun on(ctx: Context) {
        if (!ctx.server.connected || !ctx.server.playing) {
            ctx.reply("No music is playing in this guild!")
            return
        }
        ctx.server.stop()
        launch(CommonPool) {
            ctx.server.close()
            ctx.reply("Music stopped! :warning: **THIS NO LONGER CLEARS THE SONG QUEUE!** Use `!!!clear` to do this.")
        }
    }
}
