package ovh.not.dabbot

import com.moandjiezana.toml.Toml
import net.dv8tion.jda.core.events.guild.GuildJoinEvent
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import java.util.*
import java.util.regex.Pattern

class Listener(val shard: ShardManager.Shard, val commandManager: CommandManager, config: Toml): ListenerAdapter() {
    val commandPattern: Pattern

    init {
        val propertiesConfig = config.getTable("properties")
        commandPattern = Pattern.compile(propertiesConfig.getString("regex"))
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
        var matches = matcher.group(2).split("\\s+")
        if (matches.size > 0 && matches[0].equals("")) {
            matches = ArrayList<String>(0)
        }
        val ctx = Command.Context(shard, event, matches)
        cmd.on(ctx)
    }

    override fun onGuildJoin(event: GuildJoinEvent?) {

    }

    override fun onGuildLeave(event: GuildLeaveEvent?) {

    }
}