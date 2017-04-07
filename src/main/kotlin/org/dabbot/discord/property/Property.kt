@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package org.dabbot.discord.property

import org.dabbot.discord.Server

abstract class Property internal constructor(protected val server: Server, internal val key: String) {
    internal abstract fun invalidateCache()

    protected suspend fun getProperty(): String? {
        return server.propertyManager.get(key)
    }

    protected suspend fun setProperty(value: String) {
        server.propertyManager.set(key, value)
        invalidateCache()
    }
}