package org.dabbot.discord

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.produce
import org.json.JSONObject

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class Queue(val requester: Requester, val server: Server) {
    val serverId: String = server.guild.id

    private fun loadSong(o: JSONObject): QueueSong {
        val song = QueueSong(o)
        song.load(server.playerManager)
        return song
    }

    suspend fun size(): Int {
        val r = requester.execute(Method.GET, "/queues/$serverId/size")
        if (r.code() == 400) {
            // todo handle
            r.close()
            return 0
        }
        val size = JSONObject(r.body().string()).getInt("size")
        r.close()
        return size
    }

    suspend fun list(limit: Int, offset: Int) = produce(CommonPool) {
        val r = requester.execute(Method.GET, "/queues/$serverId?limit=$limit&offset=$offset")
        if (r.code() == 400) {
            // todo handle
            r.close()
            close()
            return@produce
        }
        val array = JSONObject(r.body().string()).getJSONArray("queue")
        r.close()
        repeat(array.length()) { i ->
            send(QueueSong(array[i] as JSONObject))
        }
        close()
    }

    suspend fun list() = list(0, 0)

    suspend fun next(): QueueSong? {
        val r = requester.execute(Method.GET, "/queues/$serverId/next")
        if (r.code() == 400) {
            // no songs left in queue
            // TODO fire event
            println(r.body().string())
            r.close()
            server.stop()
            server.close()
            return null
        }
        val song = loadSong(JSONObject(r.body().string()))
        r.close()
        return song
    }

    suspend fun peek(): QueueSong? {
        val r = requester.execute(Method.GET, "/queues/$serverId/peek")
        if (r.code() == 400) {
            // no songs left in queue
            // TODO fire event
            r.close()
            return null
        }
        val song = loadSong(JSONObject(r.body().string()))
        r.close()
        return song
    }

    suspend fun current(): QueueSong? {
        val r = requester.execute(Method.GET, "/queues/$serverId/current")
        if (r.code() == 400) {
            r.close()
            return null
        }
        val song = loadSong(JSONObject(r.body().string()))
        r.close()
        return song
    }

    suspend fun add(song: QueueSong) {
        val r = requester.executeJSON(Method.POST, "/queues/$serverId/add", song.toJson())
        if (r.code() != 200) {
            // todo
        }
        val o = JSONObject(r.body().string())
        r.close()
        val id = o.getLong("id")
        song.queueSongId = id
    }

    suspend fun move(song: QueueSong, position: Int) {
        val body = JSONObject()
                .put("song_id", song.queueSongId)
                .put("position", position)
        val r = requester.executeJSON(Method.PUT, "/queues/$serverId/move", body)
        if (r.code() != 200) {
            // todo
        }
        r.close()
    }

    suspend fun clear() {
        val r = requester.execute(Method.DELETE, "/queues/$serverId/clear")
        if (r.code() != 200) {
            // todo
        }
        r.close()
    }

    suspend fun shuffle() {
        val r = requester.execute(Method.GET, "/queues/$serverId/shuffle")
        if (r.code() != 200) {
            // todo
        }
        r.close()
    }

    suspend fun delete(position: Int) {
        val r = requester.executeJSON(Method.DELETE, "/queues/$serverId/position", JSONObject()
                .put("position", position))
        if (r.code() != 200) {
            // todo
        }
        r.close()
    }
}