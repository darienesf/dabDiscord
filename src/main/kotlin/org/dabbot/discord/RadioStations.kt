@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package org.dabbot.discord

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.produce
import org.json.JSONObject

class RadioStations internal constructor(private val requester: Requester) {
    suspend fun getStations(country: String?, genre: String?, name: String?) = produce(CommonPool) {
        var queryString = ""
        if (country != null) {
            queryString = "?country=$country"
        } else if (genre != null) {
            queryString = "?genre=$genre"
        } else if (name != null) {
            queryString = "?name=$name"
        }
        val r = requester.execute(Method.GET, "/stations$queryString")
        if (r.code() == 400) {
            // todo handle
            r.close()
            close()
            return@produce
        }
        val array = JSONObject(r.body().string()).getJSONArray("stations")
        for (item in array) {
            if (item is JSONObject) {
                send(RadioStation(item))
            }
        }
        close()
    }

    suspend fun getStation(id: String): RadioStation? {
        val r = requester.execute(Method.GET, "/stations/$id")
        if (r.code() == 400) {
            // todo handle
            r.close()
            return null
        }
        return RadioStation(JSONObject(r.body().string()))
    }
}