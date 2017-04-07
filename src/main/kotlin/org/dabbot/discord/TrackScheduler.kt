@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package org.dabbot.discord

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.dabbot.discord.property.Repeats

class TrackScheduler(val server: Server): AudioEventAdapter() {
    override fun onTrackStart(player: AudioPlayer?, track: AudioTrack?) {
    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        if (!endReason!!.mayStartNext) {
            return
        }
        launch(CommonPool) {
            val property = server.properties["repeat"] as Repeats
            if (track != null && property.isRepeating()) {
                player!!.playTrack(track.makeClone())
            } else {
                val next = server.queue!!.next()
                if (next != null)
                    server.play(next)
            }
        }
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