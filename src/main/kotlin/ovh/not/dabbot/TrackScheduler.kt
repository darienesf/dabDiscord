package ovh.not.dabbot

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import java.util.function.Consumer

class TrackScheduler(val server: Server): AudioEventAdapter() {
    override fun onTrackStart(player: AudioPlayer?, track: AudioTrack?) {
    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        if (!endReason!!.mayStartNext) {
            return
        }
        server.queue!!.next(Consumer { song ->
            server.play(song)
        })
    }

    override fun onPlayerPause(player: AudioPlayer?) {
    }
    override fun onPlayerResume(player: AudioPlayer?) {
    }

    override fun onTrackStuck(player: AudioPlayer?, track: AudioTrack?, thresholdMs: Long) {
    }

    override fun onTrackException(player: AudioPlayer?, track: AudioTrack?, exception: FriendlyException?) {
    }
}