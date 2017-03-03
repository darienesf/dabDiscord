package ovh.not.dabbot

import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.VoiceChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.exceptions.PermissionException
import org.apache.commons.lang3.exception.ExceptionUtils
import java.util.logging.Logger

abstract class Command(name: String, vararg names: String) {
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

    class Context(val shard: ShardManager.Shard, val event: MessageReceivedEvent, val args: List<String>) {
        val server = shard.serverManager?.getOrCreate(event.guild)!!

        fun reply(message: String, callback: ((Message?) -> Unit)?) {
            try {
                val action = event.textChannel.sendMessage(message)
                action.queue(callback)
            } catch (e: PermissionException) {
                // todo
            } catch (e: Exception) {
                Logger.getLogger(javaClass.name).warning { ExceptionUtils.getStackTrace(e) }
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