package org.dabbot.discord.command

import org.dabbot.discord.Command
import org.dabbot.discord.Permission

class ChooseCommand: Command(Permission.PLAY, "choose", "pick", "select", "cancel", "c", "choos", "chose") {
    override fun on(ctx: Context) {
        if (!ctx.server.selectors.containsKey(ctx.event.author)) {
            ctx.reply("You do not have a selector in this guild!")
            return
        }
        val selector = ctx.server.selectors[ctx.event.author]
        if (ctx.args.isEmpty()) {
            selector!!.cancel()
            return
        }
        val num = ctx.args[0].toIntOrNull()
        if (num == null || num < 1 || num > selector!!.limit) {
            ctx.reply("Invalid input `$num`. Must be integer within the range 1 - ${selector!!.limit}." +
                    "\n**To cancel selection**, use `!!!cancel`.")
        }
        selector.choose(num!!)
    }
}