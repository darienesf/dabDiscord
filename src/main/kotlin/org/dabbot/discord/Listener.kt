package org.dabbot.discord

import com.moandjiezana.toml.Toml
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.events.guild.GuildJoinEvent
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import org.dabbot.discord.property.Prefix
import java.net.ConnectException
import java.time.OffsetDateTime

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class Listener(val shard: Shard, val commandManager: CommandManager, config: Toml): ListenerAdapter() {
    val developerMode: Boolean
    val defaultPrefix: String
    val admins: List<String>

    init {
        val propertiesConfig = config.getTable("propertyManager")
        developerMode = propertiesConfig.getBoolean("developerMode")
        defaultPrefix = propertiesConfig.getString("prefix")
        admins = config.getTable("discord").getList<String>("admins")
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val author = event.author
        if (author.isBot || author.id.equals(event.jda.selfUser.id, true)) {
            return
        }
        var content = event.message.content
        launch(CommonPool) {
            try {
                val server = shard.serverManager?.get(event.guild)!!
                val prefix = (server.properties["prefix"] as Prefix).getPrefixOrElse(defaultPrefix)
                if (content.length <= prefix.length || !content.startsWith(prefix)) {
                    return@launch
                }
                if (!event.guild.selfMember.hasPermission(event.textChannel, Permission.MESSAGE_WRITE)) {
                    return@launch
                }
                content = content.substring(prefix.length)
                val matches = content.split(Regex("\\s+"))
                if (matches.isEmpty()) {
                    return@launch
                }
                val cmd = commandManager.get(matches[0].toLowerCase())?: return@launch
                val ctx = Command.Context(shard, event, matches.subList(1, matches.size))
                val isAdmin = admins.contains(ctx.event.member.user.id)
                if (cmd.permission == null && !isAdmin) {
                    return@launch
                } else if (cmd.permission != null && !ctx.server.permissions.hasPermission(event.member, cmd.permission) && !isAdmin) {
                    ctx.reply("You do not have permission to do that! Use `!!!permissions` to learn about dabBot's permission system.")
                    return@launch
                }
                cmd.on(ctx)
            } catch (e: ConnectException) {
                event.textChannel.sendMessage("Could not communicate with dabBot gateway!").queue()
                e.printStackTrace()
            }
        }
    }

    override fun onGuildJoin(event: GuildJoinEvent) {
        if (event.guild.selfMember.joinDate.isBefore(OffsetDateTime.now().minusMinutes(10))) return
        // todo send guild join message
    }
}