package ovh.not.dabbot.command

import org.json.JSONArray
import ovh.not.dabbot.Command

class IgnoreCommand: Command("ignore", "ignor", "blacklist", "channelignore", "igore") {
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
        ctx.server.properties.get("channelignore", { result ->
            val json: JSONArray
            if (result != null) {
                json = JSONArray(result)
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
            ctx.server.properties.set("channelignore", json.toString(), {
                if (ignore) {
                    ctx.reply("Now ignoring <#${channel.id}>!")
                } else {
                    ctx.reply("No longer ignoring <#${channel.id}>!")
                }
            })
        })
    }
}