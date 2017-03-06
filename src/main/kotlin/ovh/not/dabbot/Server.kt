package ovh.not.dabbot

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.entities.User
import net.dv8tion.jda.core.entities.VoiceChannel
import org.json.JSONObject

class Server(val manager: ServerManager, val requester: Requester, val guild: Guild, val playerManager: AudioPlayerManager) {
    val audioPlayer: AudioPlayer = playerManager.createPlayer()
    var queue: Queue? = Queue(requester, this)
    val properties: ServerProperties = ServerProperties(requester, this)
    var voiceChannel: VoiceChannel? = null
    val selectors: MutableMap<User, Selector<Song>> = HashMap()
    var playing = false
    var connected = false
    var lastTextChannel: TextChannel? = null

    init {
        audioPlayer.addListener(TrackScheduler(this))
        guild.audioManager.sendingHandler = AudioPlayerSendHandler(audioPlayer)
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

    fun open(voiceChannel: VoiceChannel) {
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

    fun close() {
        guild.audioManager.closeAudioConnection()
        voiceChannel = null
        playing = false
        connected = false
        updateVoiceChannel()
    }

    private fun updateVoiceChannel() {
        val body = JSONObject().put("voice_channel", voiceChannel?.id)
        val r = requester.executeJSON(Method.PUT, "/servers/" + guild.id, body)
        if (r.code() != 200) {
            // something fucked up
        }
        r.close()
    }

    fun delete() {
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

    fun play(song: QueueSong) {
        if (!connected) {
            return
        }
        if (audioPlayer.startTrack(song.track, false)) {
            playing = true

            properties.get("announcements", { result ->
                val json = JSONObject(result)
                val msg = "Now playing **%s** by **%s**".format(song.title, song.author)
                when (json.getString("type")) {
                    "normal" -> {
                        val channel: TextChannel
                        if (lastTextChannel == null) {
                            channel = guild.publicChannel
                        } else {
                            channel = lastTextChannel!!
                        }
                        channel.sendMessage(msg).queue()
                    }
                    "channel" -> {
                        val channelId = json.getString("channel")
                        val channel = guild.getTextChannelById(channelId)
                        channel.sendMessage(msg).queue()
                    }
                    "none" -> {}
                }
            })
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