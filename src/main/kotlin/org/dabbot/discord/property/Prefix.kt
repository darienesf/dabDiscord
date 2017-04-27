package org.dabbot.discord.property

import org.dabbot.discord.Server

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class Prefix internal constructor(server: Server): Property(server, "prefix") {
    var cache: String? = null
    var cached = false

    override fun invalidateCache() {
        cache = null
        cached = false
    }

    suspend fun getPrefix(): String? {
        if (cached) {
            return cache
        }
        cache = getProperty()
        cached = true
        return cache
    }

    suspend fun getPrefixOrElse(default: String): String {
        return getPrefix()?: default
    }

    suspend fun setPrefix(prefix: String) {
        setProperty(prefix)
    }

    suspend fun hasPrefix(): Boolean {
        return getPrefix() != null
    }
}