package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.entities.TextChannel
import org.dabbot.discord.Command
import org.dabbot.discord.Permission
import org.dabbot.discord.property.Announcements

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class AnnouncementsCommand: Command(Permission.ADMIN, "announcements", "anouncements", "announcement", "setmusic", "setchannel", "musicchannel", "musicchanel", "annoncements") {
    override fun on(ctx: Context) {
        val property = ctx.server.properties["announcements"] as Announcements
        launch(CommonPool) {
            if (ctx.args.isEmpty()) {
                val config = property.get()
                val builder = StringBuilder("Announcements:")
                builder.append("\nCurrent setting: `${config.type.name.toLowerCase()}`")
                ctx.reply(builder.toString())
                return@launch
            }
            val type: Announcements.Type
            try {
                type = Announcements.Type.valueOf(ctx.args[0].toUpperCase())
            } catch (e: IllegalArgumentException) {
                ctx.reply("usage lol") // todo usage
                return@launch
            }
            val channel: TextChannel?
            if (type == Announcements.Type.CHANNEL) {
                if (ctx.args.size == 2) {
                    var name = ctx.args[1]
                    if (name[0] == '#') {
                        name = name.substring(1)
                    }
                    val channels = ctx.server.guild.getTextChannelsByName(name, true)
                    if (channels == null || channels.size == 0) {
                        ctx.reply("Could not find the channel #$name!")
                        return@launch
                    }
                    channel = channels[0]
                    if (!ctx.event.guild.selfMember.hasPermission(channel, net.dv8tion.jda.core.Permission.MESSAGE_WRITE)) {
                        ctx.reply("dabBot cannot send messages in <#${channel.id}> so announcements cannot be setup!")
                        return@launch
                    }
                } else {
                    channel = ctx.event.textChannel
                }
            } else {
                channel = null
            }
            val config = Announcements.Config(type, channel)
            property.set(config)
            if (channel == null) {
                ctx.reply("Setup announcements!")
            } else {
                ctx.reply("Setup announcements in <#${channel.id}>!")
            }
        }
    }
}