@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.dabbot.discord.Command
import org.dabbot.discord.Permission

class ShuffleCommand: Command(Permission.MOD, "shuffle", "shufle", "mix", "shuffl") {
    override fun on(ctx: Context) {
        launch(CommonPool) {
            ctx.server.queue!!.shuffle()
            ctx.reply("Queue shuffled!")
        }
    }
}
