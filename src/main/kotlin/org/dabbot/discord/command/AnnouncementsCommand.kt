package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.exceptions.PermissionException
import org.dabbot.discord.Command
import org.dabbot.discord.Permission
import org.json.JSONObject

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class AnnouncementsCommand: Command(Permission.ANNOUNCEMENTS, "announcements", "anouncements", "announcement", "setmusic", "setchannel", "musicchannel", "musicchanel", "annoncements") {
    override fun on(ctx: Context) {
        if (!ctx.event.member.hasPermission(net.dv8tion.jda.core.Permission.ADMINISTRATOR)) {
            ctx.reply("Only the server owner or users with the `Administrator` permission can use this command.")
            return
        }
        if (ctx.args.isEmpty()) {
            ctx.reply("This command is for setting up dabBot's announcement messages!" +
                    "\nUsage: `!!!announcements <normal/channel/none>`" +
                    "\n**normal**: announce songs in the channel the last command was ran in" +
                    "\n**channel**: announce in a specific channel" +
                    "\n**none**: no announcements")
            return
        }
        val json = JSONObject()
        when (ctx.args[0].toLowerCase()) {
            "normal" -> {}
            "channel" -> {
                if (ctx.args.size != 2) {
                    ctx.reply("Usage: `!!!announcements channel <channel name>`\nExample: `!!!announcements channel #music-log`")
                    return
                }
                var name = ctx.args[1]
                if (name[0] == '#') {
                    name = name.substring(1)
                }
                val channels = ctx.event.guild.getTextChannelsByName(name, true)
                if (channels.isEmpty()) {
                    ctx.reply("No text channels found by that name!")
                    return
                }
                val channel = channels[0]
                try {
                    channel.sendMessage("Setup <#${channel.id}> as the dabBot music announcements channel!").complete()
                } catch (e: PermissionException) {
                    ctx.reply("dabBot does not have permission to send messages in that channel!")
                    return
                }
                json.put("channel", channel.id)
            }
            "none" -> {}
            else -> {
                ctx.reply("This command is for setting up dabBot's announcement messages!" +
                        "\nUsage: `!!!announcements <normal/channel/none>`" +
                        "\n`normal`: announce songs in the current channel" +
                        "\n`channel`: announce songs in the specified channel" +
                        "\n`none`: no announcements")
                return
            }
        }
        json.put("type", ctx.args[0].toLowerCase())
        launch(CommonPool) {
            ctx.server.properties.set("announcements", json.toString())
            ctx.reply("Setup announcements!")
        }
    }
}