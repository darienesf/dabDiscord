@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package org.dabbot.discord.property

import org.dabbot.discord.Server

class Repeats internal constructor(server: Server): Property(server, "repeat") {
    private var cache: Boolean? = null

    override fun invalidateCache() {
        cache = null
    }

    suspend fun isRepeating(): Boolean {
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

    suspend fun setRepeating(repeat: Boolean) {
        setProperty(repeat.toString())
    }
}