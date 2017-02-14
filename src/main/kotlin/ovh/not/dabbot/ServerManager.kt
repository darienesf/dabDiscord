package ovh.not.dabbot

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import net.dv8tion.jda.core.entities.Guild
import java.util.*

class ServerManager(val requester: Requester, val playerManager: AudioPlayerManager) {
    private val servers: MutableMap<String, Server> = HashMap()
    
    fun get(guild: Guild): Server? {
        return servers[guild.id]
    }

    fun getOrCreate(guild: Guild): Server {
        return servers.getOrPut(guild.id, { -> Server(requester, guild, playerManager)})
    }
}