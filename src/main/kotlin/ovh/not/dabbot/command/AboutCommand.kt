package ovh.not.dabbot.command

import ovh.not.dabbot.Command

class AboutCommand: Command("about", "info", "support", "abot", "abut", "spport", "suppot") {
    val msg = ""

    override fun on(ctx: Context) = ctx.reply(msg)
}