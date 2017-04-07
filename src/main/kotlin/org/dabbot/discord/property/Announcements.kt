@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package org.dabbot.discord.property

import net.dv8tion.jda.core.entities.TextChannel
import org.dabbot.discord.Server
import org.json.JSONObject

class Announcements internal constructor(server: Server): Property(server, "announcements") {
    enum class Type {
        NORMAL, CHANNEL, DEFAULT
    }

    class Config(var type: Type, var channel: TextChannel?) {
        override fun toString(): String {
            val json = JSONObject().put("type", type.name)
            if (channel != null) {
                json.put("channel", channel!!.id)
            }
            return json.toString()
        }
    }

    private var cache: Config? = null

    override fun invalidateCache() {
        cache = null
    }

    suspend fun get(): Config {
        if (cache != null) {
            return cache!!
        }
        val v = getProperty()
        if (v == null) {
            cache = Config(Type.NORMAL, null)
            return cache!!
        }
        val json = JSONObject(v)
        val type = Type.valueOf(json.getString("type"))
        val channel: TextChannel?
        if (json.isNull("channel")) {
            channel = null
        } else {
            channel = server.guild.getTextChannelById(json.getString("channel"))
        }
        val config = Config(type, channel)
        cache = config
        return config
    }

    suspend fun set(config: Config) {
        setProperty(config.toString())
    }
}