package ovh.not.dabbot

import org.json.JSONObject

class Queue(val requester: Requester, val server: Server) {
    val serverId: String = server.guild.id
    // todo get next song ready

    private fun loadSong(o: JSONObject): QueueSong {
        val song = QueueSong(o)
        song.load(server.playerManager)
        return song
    }

    fun next(callback: (QueueSong?) -> Unit) {
        val r = requester.execute(Method.GET, "/queues/$serverId/next")
        if (r.code() == 400) {
            // no songs left in queue
            r.close()
            server.stop()
            server.close()
            callback.invoke(null)
            return
        }
        val song = loadSong(JSONObject(r.body().string()).getJSONObject("song"))
        r.close()
        callback.invoke(song)
    }

    fun current(callback: (QueueSong?) -> Unit) {
        val r = requester.execute(Method.GET, "/queues/$serverId/current")
        if (r.code() == 400) {
            r.close()
            callback.invoke(null)
            return
        }
        val song = loadSong(JSONObject(r.body().string()).getJSONObject("song"))
        r.close()
        callback.invoke(song)
    }

    fun add(song: QueueSong) {
        val r = requester.execute(Method.POST, "/queues/$serverId/add", song.toJson())
        if (r.code() != 200) {
            // todo
        }
        val o = JSONObject(r.body().string())
        r.close()
        val songId = o.getLong("song_id")
        val queueSongId = o.getLong("queue_song_id")
        val position = o.getInt("position")
        song.id = queueSongId
    }

    fun move(song: QueueSong, position: Int) {
        val body = JSONObject()
                .put("song_id", song.id)
                .put("position", position)
        val r = requester.execute(Method.PUT, "/queues/$serverId/move", body)
        if (r.code() != 200) {
            // todo
        }
        r.close()
    }

    fun clear() {
        val r = requester.execute(Method.DELETE, "/queues/$serverId/clear")
        if (r.code() != 200) {
            // todo
        }
        r.close()
    }

    fun shuffle() {
        val r = requester.execute(Method.GET, "/queues/$serverId/shuffle")
        if (r.code() != 200) {
            // todo
        }
        r.close()
    }
}