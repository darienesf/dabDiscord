@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package org.dabbot.discord.properties

import net.dv8tion.jda.core.entities.TextChannel
import org.dabbot.discord.Server
import org.json.JSONArray

class ChannelIgnores(server: Server): Property(server, "channelignore") {
    private var cache: JSONArray? = null

    override fun invalidateCache() {
        cache = null
    }

    suspend fun isIgnoring(channel: TextChannel): Boolean {
        if (cache != null) {
            return cache!!.contains(channel.id)
        }
        val json = getProperty()
        if (json == null) {
            cache = JSONArray()
            return false
        }
        val array = JSONArray(json)
        cache = array
        return array.contains(channel.id)
    }

    suspend fun setIgnoring(channel: TextChannel, ignore: Boolean) {
        val json = cache ?: JSONArray()
        if (ignore) {
            json.put(channel.id)
        } else {
            for (i in 0..json.length() - 1) {
                if (json[i] == channel.id) {
                    json.remove(i)
                    break
                }
            }
        }
        setProperty(json.toString())
    }
}