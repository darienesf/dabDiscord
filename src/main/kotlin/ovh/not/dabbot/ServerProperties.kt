package ovh.not.dabbot

class ServerProperties(val requester: Requester, val server: Server) {
    fun invalidateCache() {
    }

    private fun getProperty(property: String, callback: (String?) -> Unit) {
        val r = requester.execute(Method.GET, "/properties/${server.guild.id}/$property")
        if (r.code() != 200) {
            r.close()
            callback(null)
            return
        }
        callback(r.body().string())
        r.close()
    }

    private fun addProperty(property: String, callback: () -> Unit) {
        val r = requester.execute(Method.POST, "/properties/${server.guild.id}/$property")
        if (r.code() != 200) {
            // o shit dude do something
        }
        r.close()
        callback()
    }

    private fun updateProperty(property: String, callback: () -> Unit) {
        val r = requester.execute(Method.PUT, "/properties/${server.guild.id}/$property")
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