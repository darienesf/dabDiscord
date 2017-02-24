package ovh.not.dabbot

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.VoiceChannel
import org.json.JSONObject

class Server(val requester: Requester, val guild: Guild, val playerManager: AudioPlayerManager) {
    private val audioPlayer: AudioPlayer = playerManager.createPlayer()
    var queue: Queue? = Queue(requester, this)
    var voiceChannel: VoiceChannel? = null
    var playing = false
    var connected = false

    init {
        audioPlayer.addListener(TrackScheduler(this))
        guild.audioManager.sendingHandler = AudioPlayerSendHandler(audioPlayer)
        val body = JSONObject().put("id", guild.id).put("owner", guild.owner.user.id)
        val r = requester.execute(Method.POST, "/servers/add", body)
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
        val r = requester.execute(Method.PUT, "/servers/" + guild.id, body)
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
            if (guild.id == "272410239947767808") {
                val msg = "Now playing **%s** by **%s**".format(song.title, song.author)
                guild.getTextChannelById("272410331450703873").sendMessage(msg).queue()
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