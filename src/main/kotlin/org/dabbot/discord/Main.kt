package org.dabbot.discord

import com.moandjiezana.toml.Toml
import okhttp3.OkHttpClient
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger

private val configFileName = "config.toml"

fun main(args: Array<String>) {
    Logger.getLogger(OkHttpClient::class.java.name).level = Level.FINE
    val config = Toml().read(File(configFileName))
    if (args.isEmpty()) {
        Shard(config)
    } else {
        val shardCount = Integer.parseInt(args[0])
        val shardId = Integer.parseInt(args[1])
        Shard(config, shardCount, shardId)
    }
}