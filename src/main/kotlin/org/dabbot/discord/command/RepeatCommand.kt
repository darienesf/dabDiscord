package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.dabbot.discord.Command
import org.dabbot.discord.Permission

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class RepeatCommand: Command(Permission.REPEAT, "repeat", "repeatsong", "rs", "loop", "cycle") {
    override fun on(ctx: Context) {
        if (ctx.args.isEmpty()) {
            ctx.reply("Usage: `!!!repeat <song/queue>`\n`song` = repeat just this song." +
                    "\n`queue` = repeat the whole song queue.")
            return
        }
        val subCommand = ctx.args[0].toLowerCase()
        launch(CommonPool) {
            if (subCommand == "song" || subCommand == "s") {
                val property = ctx.server.properties.get("repeat")
                val newVal: String
                if (property == null || property == "false") {
                    newVal = "true"
                } else {
                    newVal = "false"
                }
                ctx.server.properties.set("repeat", newVal)
                ctx.reply("Set song looping to `$newVal`!")
            } else if (subCommand == "queue" || subCommand == "q") {
                val property = ctx.server.properties.get("loop")
                val newVal: String
                if (property == null || property == "false") {
                    newVal = "true"
                } else {
                    newVal = "false"
                }
                ctx.server.properties.set("loop", newVal)
                ctx.reply("Set queue looping to `$newVal`!")
            } else {
                ctx.reply("Usage: `!!!repeat <song/queue>`\n`song` = repeat just this song." +
                        "\n`queue` = repeat the whole song queue.")
            }
        }
    }
}