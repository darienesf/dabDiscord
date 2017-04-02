package org.dabbot.discord

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason

class TrackScheduler(val server: Server): AudioEventAdapter() {
    override fun onTrackStart(player: AudioPlayer?, track: AudioTrack?) {
    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        if (!endReason!!.mayStartNext) {
            return
        }
        /*launch(CommonPool) {
            val repeat = server.properties.get("repeat")
            if (repeat != null && repeat == "true" && track != null) {
                val newTrack = track.makeClone()
                player!!.playTrack(newTrack)
            } else {
                server.play(server.queue!!.next()!!)
            }
        }*/
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