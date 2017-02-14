package ovh.not.dabbot

import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.VoiceChannel
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.exceptions.PermissionException
import java.util.function.Consumer

abstract class Command(name: String, vararg names: String) {
    val names: Array<String?>
    var hide = false

    init {
        this.names = kotlin.arrayOfNulls<String?>(names.size + 1)
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

        fun reply(message: String, callback: Consumer<Message>?) {
            try {
                val action = event.textChannel.sendMessage(message)
                if (callback != null) {
                    action.queue(callback)
                } else {
                    action.queue()
                }
            } catch (e: PermissionException) {
                // todo send u dont have perms lol
            }
        }

        fun reply(message: String) {
            reply(message, null)
        }

        fun isUserInVoiceChannel(): Boolean {
            return event.member.voiceState.inVoiceChannel()
        }

        fun getUserVoiceChannel(): VoiceChannel? {
            return event.member.voiceState.channel
        }
    }
}