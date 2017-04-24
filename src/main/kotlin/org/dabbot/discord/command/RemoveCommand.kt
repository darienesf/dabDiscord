package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.dabbot.discord.Command
import org.dabbot.discord.Permission

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class RemoveCommand: Command(Permission.MOD, "remove", "delete", "rm", "remov", "remve", "del", "delet", "delte", "dlete") {
    override fun on(ctx: Context) {
        if (!ctx.server.connected || !ctx.server.playing) {
            ctx.reply("No music is playing!")
            return
        }
        if (ctx.args.isEmpty()) {
            ctx.reply("Usage: `!!!remove <song position>`")
            return
        }
        val position = ctx.args[0].toInt()
        if (position < 1) {
            ctx.reply("Position must be 1 or bigger!")
            return
        }
        launch(CommonPool) {
            ctx.server.queue!!.delete(position)
            ctx.reply("Removed song at position $position from the queue!")
        }
    }
}