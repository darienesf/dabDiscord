package org.dabbot.discord

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import net.dv8tion.jda.core.entities.Guild
import java.util.*

class ServerManager(shard: Shard, val requester: Requester, val playerManager: AudioPlayerManager) {
    private val servers: MutableMap<String, Server> = HashMap()
    
    fun get(guild: Guild): Server {
        return servers.getOrPut(guild.id, { -> Server(this, requester, guild, playerManager)})
    }
}