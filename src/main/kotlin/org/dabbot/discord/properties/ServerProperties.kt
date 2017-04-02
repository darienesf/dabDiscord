package org.dabbot.discord.properties

import org.dabbot.discord.Method
import org.dabbot.discord.Requester
import org.dabbot.discord.Server
import org.json.JSONObject

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class ServerProperties(val requester: Requester, val server: Server) {
    internal fun registerProperties(map: MutableMap<String, Property>): ServerProperties {
        fun add(vararg properties: Property) {
            for (property in properties) {
                map.put(property.key, property)
            }
        }
        add(Announcements(server), ChannelIgnores(server), Loops(server), Repeats(server))
        return this
    }

    internal suspend fun get(property: String): String? {
        val r = requester.execute(Method.GET, "/properties/${server.guild.id}/$property")
        if (r.code() != 200) {
            r.close()
            return null
        }
        val json = JSONObject(r.body().string())
        r.close()
        if (json.isNull("error")) {
            return json.getString("value")
        } else {
            println("propertyManager#get error $property: ${json.getString("error")}")
            return null
        }
    }

    internal suspend fun set(property: String, value: String) {
        val r = requester.executePlainText(Method.PUT, "/properties/${server.guild.id}/$property", value)
        if (r.code() != 200) {
            // todo o shit dude do something
        }
        r.close()
    }
}