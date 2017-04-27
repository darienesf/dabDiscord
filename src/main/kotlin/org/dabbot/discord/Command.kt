package org.dabbot.discord

import club.minnced.kjda.entities.sendTextAsync
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.VoiceChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import org.apache.commons.lang3.exception.ExceptionUtils
import org.dabbot.discord.property.Prefix
import org.slf4j.LoggerFactory

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
abstract class Command(val permission: Permission?, name: String, vararg names: String) {
    constructor(name: String, vararg names: String): this(null, name, *names)

    companion object {
        @JvmStatic private val LOG = LoggerFactory.getLogger(Command::class.java)
    }

    val names: Array<String?> = kotlin.arrayOfNulls<String?>(names.size + 1)
    var manager: CommandManager? = null

    init {
        this.names[0] = name
        var i = 1
        for (n in names) {
            this.names[i] = n
            i++
        }
    }

    abstract fun on(ctx: Context)

    class Context internal constructor(val shard: Shard, val event: MessageReceivedEvent, val args: List<String>) {
        val server = shard.serverManager?.get(event.guild)!!

        init {
            server.lastTextChannel = event.textChannel
        }

        fun reply(message: String, callback: ((Message?) -> Unit)?) {
            launch(CommonPool) {
                val msg = message.replace("%prefix%", (server.properties["prefix"] as Prefix).getPrefixOrElse(shard.prefix))
                event.textChannel.sendTextAsync { msg } then { m ->
                    callback?.invoke(m)
                } catch { e ->
                    LOG.warn(ExceptionUtils.getStackTrace(e))
                }
            }
        }

        fun reply(message: String) = reply(message, null)

        fun isUserInVoiceChannel(): Boolean {
            return event.member.voiceState.inVoiceChannel()
        }

        fun getUserVoiceChannel(): VoiceChannel? {
            return event.member.voiceState.channel
        }
    }
}