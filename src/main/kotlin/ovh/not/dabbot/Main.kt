package ovh.not.dabbot

import com.moandjiezana.toml.Toml
import okhttp3.OkHttpClient
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger

val configFileName = "config.toml"

fun main(args: Array<String>) {
    Logger.getLogger(OkHttpClient::class.java.name).level = Level.FINE
    val config = Toml().read(File(configFileName))
    if (args.isEmpty()) {
        ShardManager(config)
    } else {
        val shardCount = Integer.parseInt(args[0])
        val minShard = Integer.parseInt(args[1])
        val maxShard = Integer.parseInt(args[2])
        ShardManager(config, shardCount, minShard, maxShard)
    }
}