package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.dabbot.discord.Command
import org.dabbot.discord.Permission
import org.dabbot.discord.property.Loops
import org.dabbot.discord.property.Repeats

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class RepeatCommand: Command(Permission.MOD, "repeat", "repeatsong", "rs", "loop", "cycle") {
    override fun on(ctx: Context) {
        if (ctx.args.isEmpty()) {
            ctx.reply("Usage: `!!!repeat <song/queue>`\n`song` = repeat just this song." +
                    "\n`queue` = repeat the whole song queue.")
            return
        }
        val subCommand = ctx.args[0].toLowerCase()
        launch(CommonPool) {
            if (subCommand == "song" || subCommand == "s") {
                val property = ctx.server.properties["repeat"] as Repeats
                val repeating = !property.isRepeating()
                property.setRepeating(repeating)
                if (repeating) {
                    ctx.reply("Enabled song repeating!")
                } else {
                    ctx.reply("Disabled song repeating!")
                }
            } else if (subCommand == "queue" || subCommand == "q") {
                val property = ctx.server.properties["loop"] as Loops
                val looping = !property.isLooping()
                property.setLooping(looping)
                if (looping) {
                    ctx.reply("Enabled queue repeating!")
                } else {
                    ctx.reply("Disabled queue repeating!")
                }
            } else {
                ctx.reply("Usage: `!!!repeat <song/queue>`\n`song` = repeat just this song." +
                        "\n`queue` = repeat the whole song queue.")
            }
        }
    }
}