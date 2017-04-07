package org.dabbot.discord

import org.json.JSONObject

class RadioStation internal constructor(o: JSONObject): Song(o.getJSONObject("song")) {
    val country: String = o.getString("country")
    val genre: String = o.getString("genre")
}