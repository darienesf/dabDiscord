@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package org.dabbot.discord.properties

import org.dabbot.discord.Server

class Loops(server: Server): Property(server, "loop") {
    private var cache: Boolean? = null

    override fun invalidateCache() {
        cache = null
    }

    suspend fun isLooping(): Boolean {
        if (cache != null) {
            return cache!!
        }
        val property = getProperty()
        if (property == null) {
            cache = false
            return false
        }
        val value = property.toBoolean()
        cache = value
        return value
    }

    suspend fun setLooping(loop: Boolean) {
        setProperty(loop.toString())
    }
}