package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.dabbot.discord.Command
import org.dabbot.discord.Permission

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class ClearCommand: Command(Permission.CLEAR, "clear", "cl", "clean", "removeall", "empty") {
    override fun on(ctx: Context) {
        /*if (!ctx.server.connected || !ctx.server.playing) {
            ctx.reply("No music is playing in this guild!")
            return
        }*/
        launch(CommonPool) {
            ctx.server.queue!!.clear()
            ctx.reply("Queue cleared!")
        }
    }
}
