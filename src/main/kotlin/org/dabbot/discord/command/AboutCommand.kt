package org.dabbot.discord.command

import org.dabbot.discord.Command
import org.dabbot.discord.Permission

class AboutCommand: Command(Permission.INFO, "about", "info", "support", "abot", "abut", "spport", "suppot") {
    val msg = "**dabBot - the music bot that makes you dab**" +
            "\nCommand prefix: `!!!`" +
            "\nCommand list: `!!!help`" +
            "\nInvite me to your server: `!!!invite`" +
            "\nSupport server: https://discord.gg/MFgsBAs"

    override fun on(ctx: Context) = ctx.reply(msg)
}