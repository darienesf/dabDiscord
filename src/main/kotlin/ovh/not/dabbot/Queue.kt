package ovh.not.dabbot

import com.sedmelluq.discord.lavaplayer.track.AudioTrack

class Queue {
    fun next(): AudioTrack {
        throw UnsupportedOperationException()
    }

    fun current(): AudioTrack {
        throw UnsupportedOperationException()
    }

    fun add(track: AudioTrack) {
        throw UnsupportedOperationException()
    }

    fun move() {
        throw UnsupportedOperationException()
    }

    fun clear() {
        throw UnsupportedOperationException()
    }
}