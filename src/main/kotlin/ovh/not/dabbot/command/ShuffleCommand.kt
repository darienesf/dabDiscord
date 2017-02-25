package ovh.not.dabbot.command

import ovh.not.dabbot.Command

class ShuffleCommand: Command("shuffle", "shufle", "mix", "shuffl") {
    override fun on(ctx: Context) {
        ctx.server.queue!!.shuffle()
        ctx.reply("Queue shuffled!")
    }
}
