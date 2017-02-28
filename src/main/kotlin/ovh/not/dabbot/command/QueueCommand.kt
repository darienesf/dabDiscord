package ovh.not.dabbot.command

import ovh.not.dabbot.Command

class QueueCommand: Command("queue", "q", "list", "show", "l") {
    override fun on(ctx: Context) {
        ctx.server.queue!!.list { songs ->
            if (songs == null) {
                ctx.reply("No songs in queue!")
            } else {
                val builder = StringBuilder("Song queue:")
                var i = 0
                songs.forEach { song ->
                    i++
                    builder.append("\n").append(i).append(" ").append(song.title)
                }
                ctx.reply(builder.toString())
            }
        }
    }
}