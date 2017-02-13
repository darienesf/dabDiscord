package ovh.not.dabbot

import org.json.JSONObject
import java.util.function.BiConsumer
import java.util.function.Consumer

class Queue(val requester: Requester, val server: Server) {
    val serverId: String = server.guild.id

    fun next(callback: Consumer<QueueSong>) {
        requester.execute(Method.GET, "/queues/$serverId/next", BiConsumer { r, o ->
            if (r.code() != 200) {
                // todo
            }
            val songObject = QueueSong(o.getJSONObject("song"))
            // todo lavaplayer load
            callback.accept(songObject)
        }, Consumer { e ->
            throw e
        })
    }

    fun current(callback: Consumer<QueueSong>) {
        requester.execute(Method.GET, "/queues/$serverId/current", BiConsumer { r, o ->
            if (r.code() != 200) {
                // todo
            }
            val songObject = QueueSong(o.getJSONObject("song"))
            // todo lavaplayer load
            callback.accept(songObject)
        }, Consumer { e ->
            throw e
        })
    }

    fun add(song: QueueSong) {
        requester.execute(Method.POST, "/queues/$serverId/add", song.toJson(), BiConsumer { r, o ->
            if (r.code() != 200) {
                // todo
            }
            val songId = o.getString("song_id")
            val queueSongId = o.getString("queue_song_id")
            val position = o.getInt("position")
        }, Consumer { e ->
            throw e
        })
    }

    fun move(song: QueueSong, position: Int) {
        val body = JSONObject()
                .put("song_id", song.id)
                .put("position", position)
        requester.execute(Method.PUT, "/queues/$serverId/move", body, BiConsumer { r, o ->
            if (r.code() != 200) {
                // todo
            }
        }, Consumer { e ->
            throw e
        })
    }

    fun clear() {
        requester.execute(Method.DELETE, "/queues/$serverId/clear", BiConsumer { r, o ->
            if (r.code() != 200) {
                // todo
            }
        }, Consumer { e ->
            throw e
        })
    }
}