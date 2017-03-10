package ovh.not.dabbot

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.apache.commons.lang3.time.DurationFormatUtils

interface Song: SelectorItem {
    val source: String?
    val identifier: String?
    val encoded: String?
    val title: String?
    val author: String?
    val duration: Long?
    var track: AudioTrack?

    override fun format(): String {
        return "$title by $author `[${getFormattedDuration()}]`"
    }

    fun formatDuration(duration: Long, format: String): String {
        return DurationFormatUtils.formatDuration(duration, format)
    }

    fun getFormattedDuration() = formatDuration(duration!!, "mm:ss")

    fun getLongFormattedDuration() = formatDuration(duration!!, "HH:mm:ss")
}