package ovh.not.dabbot

import com.moandjiezana.toml.Toml
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import javax.security.auth.login.LoginException

class ShardManager {
    private val shards: Array<Shard?>
    private val useSharding: Boolean
    private val shardCount: Int
    private val toml: Toml
    private val token: String
    private val game: String

    constructor(toml: Toml) {
        this.toml = toml
        val discordConfig = toml.getTable("discord")
        this.token = discordConfig.getString("token")
        this.game = discordConfig.getString("game")
        shards = kotlin.arrayOfNulls<Shard>(1)
        useSharding = false
        shardCount = 0
        shards[0] = Shard(this)
    }

    constructor(toml: Toml, shardCount: Int, minShard: Int, maxShard: Int) {
        this.toml = toml
        val discordConfig = toml.getTable("discord")
        this.token = discordConfig.getString("token")
        this.game = discordConfig.getString("game")
        shards = kotlin.arrayOfNulls<Shard>((maxShard - minShard) + 1)
        useSharding = true
        this.shardCount = shardCount
        var index = 0
        var shardId = minShard
        while (shardId < maxShard + 1) {
            val shard = Shard(this, shardId)
            shards[index] = shard
            shardId++
            index++
        }
    }

    class Shard {
        val manager: ShardManager
        val shard: Int
        var jda: JDA? = null

        constructor(manager: ShardManager) {
            this.manager = manager
            shard = 0
            create()
        }

        constructor(manager: ShardManager, shard: Int) {
            this.manager = manager
            this.shard = shard
            create()
        }

        fun create() {
            println("Starting shard $shard...")
            val builder = JDABuilder(AccountType.BOT).setToken(manager.token).addListener(object : ListenerAdapter() {
                override fun onMessageReceived(event: MessageReceivedEvent) {
                    println(event.message.content)
                }
            })
            if (manager.useSharding) {
                builder.useSharding(shard, manager.shardCount)
            }
            try {
                jda = builder.buildBlocking()
                jda!!.presence.game = Game.of(manager.game)
            } catch (e: LoginException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        fun restart() {
            println("Shutting down shard $shard...")
            jda?.shutdown(false)
            create()
            System.out.println("Shard $shard restarted!")
        }
    }
}