package org.dabbot.discord

import org.json.JSONObject

class Queue(val requester: Requester, val server: Server) {
    val serverId: String = server.guild.id
    // todo get next song ready

    private fun loadSong(o: JSONObject): QueueSong {
        val song = QueueSong(o)
        song.load(server.playerManager)
        return song
    }

    fun list(): List<QueueSong>? {
        val r = requester.execute(Method.GET, "/queues/$serverId")
        if (r.code() == 400) {
            r.close()
            return null
        }
        val array = JSONObject(r.body().string()).getJSONArray("songs")
        r.close()
        if (array.length() == 0) {
            return null
        }
        val songs = ArrayList<QueueSong>(array.length())
        var i = 0
        while (i < array.length()) {
            val song = QueueSong(array.getJSONObject(i))
            songs.add(song)
            i++
        }
        return songs
    }

    fun next(): QueueSong? {
        val r = requester.execute(Method.GET, "/queues/$serverId/next")
        if (r.code() == 400) {
            // no songs left in queue
            // TODO fire event
            r.close()
            server.stop()
            server.close()
            return null
        }
        val song = loadSong(JSONObject(r.body().string()).getJSONObject("song"))
        r.close()
        return song
    }

    fun current(): QueueSong? {
        val r = requester.execute(Method.GET, "/queues/$serverId/current")
        if (r.code() == 400) {
            r.close()
            return null
        }
        val song = loadSong(JSONObject(r.body().string()).getJSONObject("song"))
        r.close()
        return song
    }

    fun add(song: QueueSong) {
        val r = requester.executeJSON(Method.POST, "/queues/$serverId/add", song.toJson())
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
        val r = requester.executeJSON(Method.PUT, "/queues/$serverId/move", body)
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