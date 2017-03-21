package org.dabbot.discord.command

import org.dabbot.discord.Command
import org.dabbot.discord.Permission

class ReorderCommand: Command(Permission.REORDER, "reorder", "order", "reordr", "changeorder", "swap") {
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
        val songs = ctx.server.queue!!.list()
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