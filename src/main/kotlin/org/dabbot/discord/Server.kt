package org.dabbot.discord

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.entities.User
import net.dv8tion.jda.core.entities.VoiceChannel
import org.dabbot.discord.property.Announcements
import org.dabbot.discord.property.Property
import org.dabbot.discord.property.ServerProperties
import org.json.JSONObject

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class Server(val manager: ServerManager, val requester: Requester, val guild: Guild, val playerManager: AudioPlayerManager) {
    val audioPlayer: AudioPlayer = playerManager.createPlayer()
    var queue: Queue? = Queue(requester, this)
    val properties = HashMap<String, Property>()
    internal val propertyManager: ServerProperties = ServerProperties(requester, this).registerProperties(properties)
    val radioStations = RadioStations(requester)
    var voiceChannel: VoiceChannel? = null
    val selectors: MutableMap<User, Selector> = HashMap()
    var playing = false
    var connected = false
    var lastTextChannel: TextChannel? = null

    init {
        audioPlayer.addListener(TrackScheduler(this))
        guild.audioManager.sendingHandler = org.dabbot.discord.AudioPlayerSendHandler(audioPlayer)
        val body = JSONObject().put("id", guild.id).put("owner", guild.owner.user.id)
        val r = requester.executeJSON(Method.POST, "/servers/add", body)
        if (r.code() == 200) {
            // :ok_hand:
        } else if (r.code() == 400) {
            // todo if server already exists, load in songs
        } else {
            // todo handle
        }
        r.close()
    }

    suspend fun open(voiceChannel: VoiceChannel) {
        val audioManager = guild.audioManager
        if (audioManager.isConnected) {
            return
        }
        audioManager.openAudioConnection(voiceChannel)
        audioManager.isSelfDeafened = true
        this.voiceChannel = voiceChannel
        connected = true
        updateVoiceChannel()
    }

    suspend fun close() {
        guild.audioManager.closeAudioConnection()
        voiceChannel = null
        playing = false
        connected = false
        updateVoiceChannel()
    }

    suspend private fun updateVoiceChannel() {
        val body = JSONObject().put("voice_channel", voiceChannel?.id)
        val r = requester.executeJSON(Method.PUT, "/servers/" + guild.id, body)
        if (r.code() != 200) {
            // something fucked up
        }
        r.close()
    }

    suspend fun delete() {
        queue?.clear()
        val r = requester.execute(Method.DELETE, "/servers/" + guild.id)
        if (r.code() == 200) {
            // do something
            queue = null
        } else {
            // something fucked up
        }
        r.close()
    }

    suspend fun play(song: QueueSong) {
        if (!connected) {
            return
        }
        if (audioPlayer.startTrack(song.track, false)) {
            playing = true
            var msg = "Now playing **${song.title}** "
            if (!song.track?.info?.isStream!!) {
                msg += "by **${song.author}** `[${song.getFormattedDuration()}]`"
            }
            val config = (properties["announcements"] as Announcements).get()
            when (config.type) {
                Announcements.Type.NORMAL -> {
                    val channel: TextChannel
                    if (lastTextChannel == null) {
                        channel = guild.publicChannel
                    } else {
                        channel = lastTextChannel!!
                    }
                    channel.sendMessage(msg).queue()
                }
                Announcements.Type.CHANNEL -> {
                    config.channel!!.sendMessage(msg).queue()
                }
                Announcements.Type.DEFAULT -> {}
            }
        } else {
            playing = false
            close()
        }
    }

    fun stop() {
        if (!connected) {
            return
        }
        if (audioPlayer.playingTrack == null) {
            return
        }
        audioPlayer.stopTrack()
        playing = false
    }

    fun isPaused(): Boolean {
        return audioPlayer.isPaused
    }

    fun pause() {
        if (!connected) {
            return
        }
        if (audioPlayer.isPaused) {
            return
        }
        audioPlayer.isPaused = true
    }

    fun resume() {
        if (!connected) {
            return
        }
        if (!audioPlayer.isPaused) {
            return
        }
        audioPlayer.isPaused = false
    }
}