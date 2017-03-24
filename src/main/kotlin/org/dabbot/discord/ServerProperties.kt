package org.dabbot.discord

import org.json.JSONObject

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class ServerProperties(val requester: Requester, val server: Server) {
    fun invalidateCache() {
    }

    suspend fun get(property: String): String? {
        val r = requester.execute(Method.GET, "/properties/${server.guild.id}/$property")
        if (r.code() != 200) {
            r.close()
            return null
        }
        val json = JSONObject(r.body().string())
        r.close()
        if (json.isNull("error")) {
            return json.getJSONObject("property").getString("value")
        } else {
            println("properties#get error $property: ${json.getString("error")}")
            return null
        }
    }

    suspend fun set(property: String, value: String) {
        val r = requester.executePlainText(Method.PUT, "/properties/${server.guild.id}/$property", value)
        if (r.code() != 200) {
            // o shit dude do something
        }
        r.close()
    }
}