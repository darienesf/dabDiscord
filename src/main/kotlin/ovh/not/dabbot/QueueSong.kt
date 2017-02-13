package ovh.not.dabbot

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.json.JSONObject
import java.util.*

class QueueSong: Song {
    val id: Long?
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

    constructor(id: Long) {
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