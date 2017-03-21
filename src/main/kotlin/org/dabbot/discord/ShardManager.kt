@file:Suppress("INTERFACE_STATIC_METHOD_CALL_FROM_JAVA6_TARGET")

package org.dabbot.discord

import com.moandjiezana.toml.Toml
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import org.json.JSONObject
import javax.security.auth.login.LoginException

class ShardManager {
    val shards: Array<Shard?>
    private val useSharding: Boolean
    private val shardCount: Int
    val config: Toml
    private val token: String
    private val game: String

    constructor(config: Toml) {
        this.config = config
        val discordConfig = config.getTable("discord")
        this.token = discordConfig.getString("token")
        this.game = discordConfig.getString("game")
        shards = kotlin.arrayOfNulls<Shard>(1)
        useSharding = false
        shardCount = 1
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
        var id: Int = 0
        var jda: JDA? = null
        var commandManager: CommandManager? = null
        var listener: Listener? = null
        var playerManager: AudioPlayerManager? = null
        var requester: Requester? = null
        var serverManager: ServerManager? = null

        init {
            create()
        }

        constructor(manager: ShardManager, id: Int): this(manager) {
            this.id = id
        }

        fun create() {
            println("Starting shard $id...")
            commandManager = CommandManager(manager.config)
            listener = Listener(this, commandManager!!, manager.config)
            playerManager = DefaultAudioPlayerManager()
            AudioSourceManagers.registerRemoteSources(playerManager)
            val apiTable = manager.config.getTable("api")
            requester = Requester(apiTable.getString("url"), apiTable.getString("token"))
            serverManager = ServerManager(manager, requester!!, playerManager!!)
            val builder = JDABuilder(AccountType.BOT).setToken(manager.token).addListener(listener)
                    .setAudioEnabled(true)
            if (manager.useSharding) {
                builder.useSharding(id, manager.shardCount)
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
            println("Shutting down shard $id...")
            jda?.shutdown(false)
            create()
            System.out.println("Shard $id restarted!")
        }

        fun updateStatistics() {
            val serverCount = jda!!.guilds.size
            var userCount = 0
            var connectionCount = 0
            jda!!.guilds.forEach { guild ->
                userCount += guild.members.size
                if (guild.audioManager.isConnected) {
                    connectionCount++
                }
            }
            val r = requester!!.executeJSON(Method.PUT, "/statistics/$id", JSONObject()
                    .put("shard_id", id)
                    .put("shard_count", manager.shardCount)
                    .put("server_count", serverCount)
                    .put("user_count", userCount)
                    .put("connection_count", connectionCount))
            if (r.code() != 200) {
                r.close()
                // do something
                return
            }
            r.close()
        }
    }
}