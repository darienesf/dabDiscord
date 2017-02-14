package ovh.not.dabbot

import com.moandjiezana.toml.Toml
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import javax.security.auth.login.LoginException

class ShardManager {
    val shards: Array<Shard?>
    private val useSharding: Boolean
    private val shardCount: Int
    private val config: Toml
    private val token: String
    private val game: String

    constructor(config: Toml) {
        this.config = config
        val discordConfig = config.getTable("discord")
        this.token = discordConfig.getString("token")
        this.game = discordConfig.getString("game")
        shards = kotlin.arrayOfNulls<Shard>(1)
        useSharding = false
        shardCount = 0
        shards[0] = Shard(this)
    }

    constructor(config: Toml, shardCount: Int, minShard: Int, maxShard: Int) {
        this.config = config
        val discordConfig = config.getTable("discord")
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

    class Shard(val manager: ShardManager) {
        var shard: Int = 0
        var jda: JDA? = null
        var commandManager: CommandManager? = null
        var listener: Listener? = null
        var playerManager: AudioPlayerManager? = null
        var requester: Requester? = null
        var serverManager: ServerManager? = null

        init {
            create()
        }

        constructor(manager: ShardManager, shard: Int): this(manager) {
            this.shard = shard
        }

        fun create() {
            println("Starting shard $shard...")
            commandManager = CommandManager(manager.config, this)
            listener = Listener(this, commandManager!!, manager.config)
            playerManager = DefaultAudioPlayerManager()
            AudioSourceManagers.registerRemoteSources(playerManager)
            val apiTable = manager.config.getTable("api")
            requester = Requester(apiTable.getString("url"), apiTable.getString("token"))
            serverManager = ServerManager(requester!!, playerManager!!)
            val builder = JDABuilder(AccountType.BOT).setToken(manager.token).addListener(listener)
                    .setAudioEnabled(true)
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