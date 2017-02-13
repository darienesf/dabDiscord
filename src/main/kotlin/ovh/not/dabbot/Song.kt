package ovh.not.dabbot

import com.sedmelluq.discord.lavaplayer.track.AudioTrack

interface Song {
    val source: String?
    val identifier: String?
    val encoded: String?
    val title: String?
    val author: String?
    val duration: Long?
    var track: AudioTrack?
}