package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.dabbot.discord.Command
import org.dabbot.discord.Permission

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class StopCommand: Command(Permission.MOD, "stop", "end", "disconnect", "close", "dc", "leave") {
    override fun on(ctx: Context) {
        ctx.server.stop()
        launch(CommonPool) {
            ctx.server.close()
            ctx.reply("Music stopped! :warning: **THIS NO LONGER CLEARS THE SONG QUEUE!** Use `!!!clear` to do this.")
        }
    }
}
