package ovh.not.dabbot.command

import com.moandjiezana.toml.Toml
import net.dv8tion.jda.core.entities.VoiceChannel
import ovh.not.dabbot.Command

class AdminCommand(private val config: Toml) : Command("admin", "a") {
    override fun on(ctx: Context) {
        if (!config.getTable("discord").getList<String>("admins").contains(ctx.event.author.id)) return
        if (ctx.args.isEmpty()) {
            ctx.reply("some usage lol")
            return
        }
        when (ctx.args[0].toLowerCase()) {
            "stop" -> {
                ctx.shard.manager.shards.forEach { shard ->
                    shard?.jda?.shutdown(false)
                }
            }
            "open" -> {
                ctx.server.open(ctx.getUserVoiceChannel() as VoiceChannel)
                ctx.reply("Connection opened!")
            }
            "close" -> {
                ctx.server.close()
                ctx.reply("Connection closed!")
            }
        }
    }
}
