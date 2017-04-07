package org.dabbot.discord

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.tools.io.MessageInput
import com.sedmelluq.discord.lavaplayer.tools.io.MessageOutput
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.apache.commons.lang3.time.DurationFormatUtils
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*

abstract class Song: SelectorItem {
    var songId: Long? = null
    val source: String?
    val identifier: String?
    var encoded: String? = null
    val title: String?
    val author: String?
    val duration: Long?
    var track: AudioTrack? = null

    constructor(o: JSONObject) {
        this.songId = o.getLong("id")
        this.source = o.getString("source")
        this.identifier = o.getString("identifier")
        this.encoded = o.getString("encoded")
        val meta = o.getJSONObject("meta")
        this.title = meta.getString("title")
        this.author = meta.getString("author")
        this.duration = meta.getLong("duration")
    }

    constructor(track: AudioTrack) {
        this.source = track.sourceManager.sourceName
        this.identifier = track.identifier
        this.title = track.info.title
        this.author = track.info.author
        this.duration = track.duration
        this.track = track
    }

    internal fun load(playerManager: AudioPlayerManager) {
        val bytes = Base64.getDecoder().decode(encoded!!.toByteArray())
        val holder = playerManager.decodeTrack(MessageInput(bytes.inputStream()))
        track = holder.decodedTrack
    }

    internal fun encode(playerManager: AudioPlayerManager): String {
        if (encoded != null) return encoded!!
        val stream = ByteArrayOutputStream()
        playerManager.encodeTrack(MessageOutput(stream), track)
        encoded = String(Base64.getEncoder().encode(stream.toByteArray()))
        return encoded!!
    }

    override fun format(): String {
        return "$title by $author `[${getFormattedDuration()}]`"
    }

    fun formatDuration(duration: Long, format: String): String {
        return DurationFormatUtils.formatDuration(duration, format)
    }

    fun getFormattedDuration() = formatDuration(duration!!, "mm:ss")

    fun getLongFormattedDuration() = formatDuration(duration!!, "HH:mm:ss")
}