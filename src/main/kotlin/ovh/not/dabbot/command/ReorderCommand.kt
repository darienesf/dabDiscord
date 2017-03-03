package ovh.not.dabbot.command

import ovh.not.dabbot.Command

class ReorderCommand: Command("reorder", "order", "reordr", "changeorder", "swap") {
    override fun on(ctx: Context) {
        if (!ctx.server.connected || !ctx.server.playing) {
            ctx.reply("No music is playing!")
            return
        }
        if (ctx.args.size < 2) {
            ctx.reply("Usage: <current song position> <new position>")
            return
        }
        val currentPosition = ctx.args[0].toInt()
        val newPosition = ctx.args[1].toInt()
        ctx.server.queue!!.list { songs ->
            if (songs == null || songs.isEmpty()) {
                ctx.reply("Song queue is empty!")
            } else {
                val song = songs.getOrNull(currentPosition - 1)
                if (song == null) {
                    ctx.reply("Invalid song!")
                } else {
                    ctx.server.queue!!.move(song, newPosition - 1)
                    ctx.reply("Song moved!")
                }
            }
        }
    }
}