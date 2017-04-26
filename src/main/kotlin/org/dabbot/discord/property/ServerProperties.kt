package org.dabbot.discord.property

import org.dabbot.discord.Method
import org.dabbot.discord.Server
import org.json.JSONArray
import org.json.JSONObject

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
internal class ServerProperties(private val server: Server) {
    internal fun registerProperties(map: MutableMap<String, Property>): ServerProperties {
        fun add(vararg properties: Property) {
            for (property in properties) {
                map.put(property.key, property)
            }
        }
        add(Announcements(server), Loops(server), Prefix(server), Repeats(server))
        return this
    }

    internal suspend fun get(property: String): String? {
        val r = server.requester.execute(Method.GET, "/properties/${server.guild.id}/$property")
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
        val r = server.requester.executePlainText(Method.PUT, "/properties/${server.guild.id}/$property", value)
        if (r.code() != 200) {
            // todo o shit dude do something
        }
        r.close()
    }

    suspend fun list(): JSONArray? {
        val r = server.requester.execute(Method.GET, "/properties/${server.guild.id}")
        if (r.code() != 200) {
            // todo do something lol
            r.close()
            return null
        }
        val json = JSONObject(r.body().string())
        r.close()
        if (!json.isNull("error")) {
            // todo do something
            return null
        }
        return json.getJSONArray("properties")
    }
}