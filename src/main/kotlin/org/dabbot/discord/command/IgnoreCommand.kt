package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.dabbot.discord.Command
import org.dabbot.discord.Permission
import org.json.JSONArray

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class IgnoreCommand: Command(Permission.IGNORE, "ignore", "ignor", "blacklist", "channelignore", "igore") {
    override fun on(ctx: Context) {
        if (!ctx.event.member.hasPermission(net.dv8tion.jda.core.Permission.ADMINISTRATOR)) {
            ctx.reply("Only the server owner or users with the `Administrator` permission can use this command.")
            return
        }
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
            val channelIgnore = ctx.server.properties.get("channelignore")
            val json: JSONArray
            if (channelIgnore != null) {
                json = JSONArray(channelIgnore)
            } else {
                json = JSONArray()
            }
            var ignore = true
            var i = 0
            for (s in json) {
                if (s == channel.id) {
                    ignore = false
                    json.remove(i)
                    break
                }
                i++
            }
            if (ignore) {
                json.put(channel.id)
            }
            ctx.server.properties.set("channelignore", json.toString())
            if (ignore) {
                ctx.reply("Now ignoring <#${channel.id}>!")
            } else {
                ctx.reply("No longer ignoring <#${channel.id}>!")
            }
        }
    }
}