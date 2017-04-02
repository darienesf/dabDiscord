package org.dabbot.discord

import club.minnced.kjda.entities.sendTextAsync
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.VoiceChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory

abstract class Command(val permission: Permission, name: String, vararg names: String) {
    companion object {
        @JvmStatic private val LOG = LoggerFactory.getLogger(Command::class.java)
    }

    val names: Array<String?> = kotlin.arrayOfNulls<String?>(names.size + 1)

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
        val server = shard.serverManager?.getOrCreate(event.guild)!!

        init {
            server.lastTextChannel = event.textChannel
        }

        fun reply(message: String, callback: ((Message?) -> Unit)?) {
            event.textChannel.sendTextAsync { message } then { m ->
                callback?.invoke(m)
            } catch { e ->
                LOG.warn(ExceptionUtils.getStackTrace(e))
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