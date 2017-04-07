package org.dabbot.discord.command

import com.moandjiezana.toml.Toml
import com.sedmelluq.discord.lavaplayer.tools.io.MessageInput
import com.sedmelluq.discord.lavaplayer.tools.io.MessageOutput
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.entities.VoiceChannel
import org.dabbot.discord.Command
import org.dabbot.discord.Permission
import org.dabbot.discord.QueueSong
import java.io.ByteArrayOutputStream
import java.util.*
import javax.script.ScriptEngineManager
import javax.script.ScriptException

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class AdminCommand(private val config: Toml) : Command(Permission.ADMIN, "admin", "a") {
    val engineManager = ScriptEngineManager()

    override fun on(ctx: Context) {
        if (!config.getTable("discord").getList<String>("admins").contains(ctx.event.author.id)) return
        if (ctx.args.isEmpty()) {
            ctx.reply("stop, open, close, js, encode, decode")
            return
        }
        when (ctx.args[0].toLowerCase()) {
            "stop" -> {
                ctx.shard.client.disconnect()
                ctx.shard.jda?.shutdown(false)
            }
            "open" -> {
                launch(CommonPool) {
                    ctx.server.open(ctx.getUserVoiceChannel() as VoiceChannel)
                    ctx.reply("Connection opened!")
                }
            }
            "close" -> {
                launch(CommonPool) {
                    ctx.server.close()
                    ctx.reply("Connection closed!")
                }
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
            "encode" -> {
                if (ctx.server.audioPlayer.playingTrack == null) {
                    ctx.reply("No track playing!")
                    return
                }
                val stream = ByteArrayOutputStream()
                ctx.server.playerManager.encodeTrack(MessageOutput(stream), ctx.server.audioPlayer.playingTrack)
                ctx.reply(String(Base64.getEncoder().encode(stream.toByteArray())))
            }
            "decode" -> {
                if (ctx.args.size < 2) {
                    ctx.reply("!!!a decode <encoded track>")
                    return
                }
                val bytes = Base64.getDecoder().decode(ctx.args[1].toByteArray())
                val holder = ctx.server.playerManager.decodeTrack(MessageInput(bytes.inputStream()))
                launch(CommonPool) {
                    val song = QueueSong(holder.decodedTrack, ctx.event.author.id)
                    ctx.server.queue!!.add(song)
                }
            }
        }
    }
}
