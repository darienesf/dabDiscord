package ovh.not.dabbot

import com.moandjiezana.toml.Toml
import java.io.File

val configFileName = "config.toml"

fun main(args: Array<String>) {
    val config = Toml().read(File(configFileName))
    if (args.size == 0) {
        ShardManager(config)
    } else {
        val shardCount = Integer.parseInt(args[0])
        val minShard = Integer.parseInt(args[1])
        val maxShard = Integer.parseInt(args[2])
        ShardManager(config, shardCount, minShard, maxShard)
    }
}