package ovh.not.dabbot.command

import com.moandjiezana.toml.Toml
import net.dv8tion.jda.core.entities.VoiceChannel
import ovh.not.dabbot.Command
import javax.script.ScriptEngineManager
import javax.script.ScriptException

class AdminCommand(private val config: Toml) : Command("admin", "a") {
    val engineManager = ScriptEngineManager()

    override fun on(ctx: Context) {
        if (!config.getTable("discord").getList<String>("admins").contains(ctx.event.author.id)) return
        if (ctx.args.isEmpty()) {
            ctx.reply("stop, open, close, js")
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
            "js" -> {
                val engine = engineManager.getEngineByName("nashorn")
                engine.put("ctx", ctx)
                engine.put("event", ctx.event)
                engine.put("shard", ctx.shard)
                try {
                    val result = engine.eval(ctx.args.slice(1..ctx.args.size-1).joinToString(" "))
                    if (result != null) {
                        ctx.reply(result.toString())
                    }
                } catch (e: ScriptException) {
                    e.printStackTrace()
                    ctx.reply(e.message!!)
                }
            }
        }
    }
}
