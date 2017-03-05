package ovh.not.dabbot.command

import ovh.not.dabbot.Command

class RepeatCommand: Command("repeat", "repeatsong", "rs") {
    override fun on(ctx: Context) {
        if (ctx.args.isEmpty()) {
            ctx.reply("Usage: `!!!repeat <song/queue>`\n`song` = repeat just this song." +
                    "\n`queue` = repeat the whole song queue.")
            return
        }
        val subCommand = ctx.args[0].toLowerCase()
        if (subCommand == "song" || subCommand == "s") {
            ctx.server.properties.get("repeat", { property ->
                val newVal: String
                if (property == null || property == "false") {
                    newVal = "true"
                } else {
                    newVal = "false"
                }
                ctx.server.properties.set("repeat", newVal, {
                    ctx.reply("Set song looping to `$newVal`!")
                })
            })
        } else if (subCommand == "queue" || subCommand == "q") {
            ctx.server.properties.get("loop", { property ->
                val newVal: String
                if (property == null || property == "false") {
                    newVal = "true"
                } else {
                    newVal = "false"
                }
                ctx.server.properties.set("loop", newVal, {
                    ctx.reply("Set queue looping to `$newVal`!")
                })
            })
        } else {
            ctx.reply("Usage: `!!!repeat <song/queue>`\n`song` = repeat just this song." +
                    "\n`queue` = repeat the whole song queue.")
        }
    }
}