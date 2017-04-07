package org.dabbot.discord

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.json.JSONObject
import java.util.*

class QueueSong: Song {
    var queueSongId: Long?
    var addedBy: String?
    var dateAdded: Date?
    var position: Int? = -1

    internal constructor(o: JSONObject): super(o.getJSONObject("song")) {
        this.queueSongId = o.getLong("id")
        this.addedBy = o.getString("added_by")
        this.dateAdded = null // todo parse date string
        this.position = o.getInt("position")
    }

    internal constructor(track: AudioTrack, addedBy: String?): super(track) {
        this.queueSongId = null // todo (db)
        this.addedBy = addedBy
        this.dateAdded = null // todo (db)
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