package org.dabbot.discord.property

import org.dabbot.discord.Server

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class Prefix internal constructor(server: Server): Property(server, "prefix") {
    var cache: String? = null

    override fun invalidateCache() {
        cache = null
    }

    suspend fun getPrefix(): String? {
        if (cache != null) {
            return cache
        }
        cache = getProperty()
        return cache
    }

    suspend fun getPrefixOrElse(default: String): String {
        return getProperty()?: default
    }

    suspend fun setPrefix(prefix: String) {
        setProperty(prefix)
    }

    suspend fun hasPrefix(): Boolean {
        return getPrefix() != null
    }
}