package ovh.not.dabbot

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.VoiceChannel
import org.json.JSONObject
import java.util.function.BiConsumer
import java.util.function.Consumer

class Server(val requester: Requester, val guild: Guild, val playerManager: AudioPlayerManager) {
    val queue: Queue? = null //TODO initialize, remove ?

    init {
        addServer()
    }

    private fun addServer() {
        val body = JSONObject().put("id", guild.id).put("owner", guild.owner.user.id)
        requester.execute(Method.POST, "/servers/add", body, BiConsumer { r, o ->
            if (r.code() == 200) {
                // :ok_hand:
            }
            if (r.code() == 400) {
                // todo if server already exists, load in songs
            }
        }, Consumer { e -> throw e })
    }

    fun open(channel: VoiceChannel) {
        throw UnsupportedOperationException()
    }

    fun close() {
        throw UnsupportedOperationException()
    }

    fun delete() {
        requester.execute(Method.DELETE, "/servers/" + guild.id, BiConsumer { r, o ->
            if (r.code() != 200) {
                // do something
            }
        }, Consumer { e -> throw e })
    }

    fun start() {
        throw UnsupportedOperationException()
    }

    fun stop() {
        throw UnsupportedOperationException()
    }
}