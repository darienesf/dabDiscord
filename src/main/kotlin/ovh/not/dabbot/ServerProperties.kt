package ovh.not.dabbot

import org.json.JSONObject

class ServerProperties(val requester: Requester, val server: Server) {
    fun invalidateCache() {
    }

    fun get(property: String, callback: (String?) -> Unit) {
        val r = requester.execute(Method.GET, "/properties/${server.guild.id}/$property")
        if (r.code() != 200) {
            r.close()
            callback(null)
            return
        }
        val json = JSONObject(r.body().string())
        r.close()
        if (json.isNull("error")) {
            callback(json.getJSONObject("property").getString("value"))
        } else {
            callback(null)
            println("properties#get error $property: ${json.getString("error")}")
        }
    }

    fun set(property: String, value: String, callback: () -> Unit) {
        val r = requester.executePlainText(Method.PUT, "/properties/${server.guild.id}/$property", value)
        if (r.code() != 200) {
            // o shit dude do something
        }
        r.close()
        callback()
    }
}