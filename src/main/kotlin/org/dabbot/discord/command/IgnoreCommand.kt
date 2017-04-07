package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.dabbot.discord.Command
import org.dabbot.discord.Permission
import org.dabbot.discord.property.ChannelIgnores

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class IgnoreCommand: Command(Permission.IGNORE, "ignore", "ignor", "blacklist", "channelignore", "igore") {
    override fun on(ctx: Context) {
        if (ctx.args.isEmpty()) {
            ctx.reply("Makes the bot ignore a channel.\nUsage: `!!!ignore <channel name>`\nExample: `!!!ignore #general`")
            return
        }
        var name = ctx.args[0]
        if (name[0] == '#') {
            name = name.substring(1)
        }
        val channels = ctx.event.guild.getTextChannelsByName(name, true)
        if (channels.isEmpty()) {
            ctx.reply("Could not find the channel #${name}!")
            return
        }
        val channel = channels[0]
        launch(CommonPool) {
            val ignores = ctx.server.properties["channelignore"] as ChannelIgnores
            val ignoring = !ignores.isIgnoring(channel)
            ignores.setIgnoring(channel, ignoring)
            if (ignoring) {
                ctx.reply("Now ignoring <#${channel.id}>!")
            } else {
                ctx.reply("No longer ignoring <#${channel.id}>!")
            }
        }
    }
}