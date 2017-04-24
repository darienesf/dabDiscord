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
import org.dabbot.discord.ipc.Client
import javax.security.auth.login.LoginException

class Shard(val config: Toml) {
    private var useSharding = false
    private var shardCount = 1
    var shard = 0
    private val token: String
    private val game: String
    val client: Client

    var jda: JDA? = null
    var commandManager: CommandManager? = null
    var listener: Listener? = null
    var playerManager: AudioPlayerManager? = null
    var requester: Requester? = null
    var serverManager: ServerManager? = null

    init {
        val discordConfig = config.getTable("discord")
        this.token = discordConfig.getString("token")
        this.game = discordConfig.getString("game")
        create()
        client = Client(this).connect()
    }

    constructor(config: Toml, shardCount: Int, shard: Int): this(config) {
        this.shardCount = shardCount
        this.shard = shard
        useSharding = true
    }

    fun create() {
        println("Starting shard $shard...")
        commandManager = CommandManager()
        listener = Listener(this, commandManager!!, config)
        playerManager = DefaultAudioPlayerManager()
        AudioSourceManagers.registerRemoteSources(playerManager)
        val apiTable = config.getTable("api")
        requester = Requester(apiTable.getString("url"), apiTable.getString("token"))
        serverManager = ServerManager(this, requester!!, playerManager!!)
        val builder = JDABuilder(AccountType.BOT).setToken(token).addListener(listener)
                .setAudioEnabled(true)
        if (useSharding) {
            builder.useSharding(shard, shardCount)
        }
        try {
            jda = builder.buildBlocking()
            jda!!.presence.game = Game.of(game)
        } catch (e: LoginException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}