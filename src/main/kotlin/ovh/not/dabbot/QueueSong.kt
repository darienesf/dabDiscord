package ovh.not.dabbot

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.tools.io.MessageInput
import com.sedmelluq.discord.lavaplayer.tools.io.MessageOutput
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*

class QueueSong: Song {
    var id: Long?
    var addedBy: String?
    var dateAdded: Date?
    override val source: String?
    override val identifier: String?
    override val encoded: String?
    override val title: String?
    override val author: String?
    override val duration: Long?
    override var track: AudioTrack? = null // todo

    constructor(o: JSONObject) {
        this.id = o.getLong("id")
        this.addedBy = o.getString("added_by")
        this.dateAdded = null // todo parse date string
        this.source = o.getString("source")
        this.identifier = o.getString("identifier")
        this.encoded = o.getString("encoded")
        this.title = o.getString("title")
        this.author = o.getString("author")
        this.duration = o.getLong("duration")
    }

    constructor(addedBy: String, dateAdded: Date, source: String, identifier: String,
                encoded: String, title: String, author: String, duration: Long) {
        this.id = null // todo
        this.addedBy = addedBy
        this.dateAdded = dateAdded
        this.source = source
        this.identifier = identifier
        this.encoded = encoded
        this.title = title
        this.author = author
        this.duration = duration
        this.track = null // todo
    }

    constructor(requester: Requester, id: Long) {
        this.id = id
        this.addedBy = null
        this.dateAdded = null
        this.source = null
        this.identifier = null
        this.encoded = null
        this.title = null
        this.author = null
        this.duration = null
        this.track = null // todo
    }

    constructor(track: AudioTrack, addedBy: String, playerManager: AudioPlayerManager) {
        this.id = null // todo (db)
        this.addedBy = addedBy
        this.dateAdded = null // todo (db)
        this.source = track.sourceManager.sourceName
        this.identifier = track.identifier
        this.encoded = encode(track, playerManager)
        this.title = track.info.title
        this.author = track.info.author
        this.duration = track.duration
        this.track = track
    }

    fun load(playerManager: AudioPlayerManager) {
        val bytes = Base64.getDecoder().decode(encoded?.toByteArray())
        val holder = playerManager.decodeTrack(MessageInput(bytes.inputStream()))
        track = holder.decodedTrack
    }

    private fun encode(track: AudioTrack, playerManager: AudioPlayerManager): String {
        val stream = ByteArrayOutputStream()
        playerManager.encodeTrack(MessageOutput(stream), track)
        return String(Base64.getEncoder().encode(stream.toByteArray()))
    }

    fun toJson(): JSONObject {
        return JSONObject()
                .put("added_by", addedBy)
                .put("source", source)
                .put("identifier", identifier)
                .put("encoded", encoded)
                .put("title", title)
                .put("author", author)
                .put("duration", duration)
    }
}