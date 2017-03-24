package org.dabbot.discord

import com.moandjiezana.toml.Toml
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.events.guild.GuildJoinEvent
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import org.json.JSONArray
import java.net.ConnectException
import java.util.regex.Pattern

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class Listener(val shard: ShardManager.Shard, val commandManager: CommandManager, config: Toml): ListenerAdapter() {
    val commandPattern: Pattern
    val developerMode: Boolean
    val splittingRegex = Regex("\\s+")

    init {
        val propertiesConfig = config.getTable("properties")
        commandPattern = Pattern.compile(propertiesConfig.getString("regex"))
        developerMode = propertiesConfig.getBoolean("developerMode")
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val author = event.author
        if (author.isBot || author.id.equals(event.jda.selfUser.id, true)) {
            return
        }
        val content = event.message.content
        val matcher = commandPattern.matcher(content.replace("\r", " ").replace("\n", " "))
        if (!matcher.find()) {
            return
        }
        val name = matcher.group(1).toLowerCase()
        val cmd = commandManager.get(name)?: return
        var matches = matcher.group(2).split(splittingRegex)
        if (matches.isNotEmpty() && matches[0] == "") {
            matches = ArrayList<String>(0)
        }
        launch(CommonPool) {
            try {
                val ctx = Command.Context(shard, event, matches)
                val channelIgnore = ctx.server.properties.get("channelignore")
                if (channelIgnore != null) {
                    val json = JSONArray(channelIgnore)
                    json.filter { it == event.textChannel.id }.first { return@launch }
                }
                if (!hasPermission(event.member, cmd.permission)) {
                    ctx.reply("You do not have permission to do that! Use `!!!permissions` to learn about dabBot's permission system.")
                } else {
                    cmd.on(ctx)
                }
            } catch (e: ConnectException) {
                event.textChannel.sendMessage("Could not communicate with dabBot gateway!").queue()
                e.printStackTrace()
            }
        }

    }

    override fun onGuildJoin(event: GuildJoinEvent?) {
        if (developerMode) return
        shard.updateStatistics()
    }

    override fun onGuildLeave(event: GuildLeaveEvent?) {
        if (developerMode) return
        shard.updateStatistics()
    }
}