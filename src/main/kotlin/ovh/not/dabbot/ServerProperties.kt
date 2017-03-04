package ovh.not.dabbot

class ServerProperties(val requester: Requester, val server: Server) {
    fun invalidateCache() {
    }

    fun getProperty(property: String, callback: (String?) -> Unit) {
        val r = requester.execute(Method.GET, "/properties/${server.guild.id}/$property")
        if (r.code() != 200) {
            r.close()
            callback(null)
            return
        }
        callback(r.body().string())
        r.close()
    }

    fun setProperty(property: String, value: String, callback: () -> Unit) {
        val r = requester.executePlainText(Method.PUT, "/properties/${server.guild.id}/$property", value)
        if (r.code() != 200) {
            // o shit dude do something
        }
        r.close()
        callback()
    }

    /*fun getPrefix(callback: (String) -> Unit) {
        if (prefix != null) {
            callback(prefix!!)
            return
        }
        getProperty("prefix", { value ->
            if (value == null) {
                prefix = defaultPrefix
            } else {
                prefix = value
            }
            callback(prefix!!)
        })
    }*/
}