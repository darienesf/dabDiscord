package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.dabbot.discord.Command
import org.dabbot.discord.Permission
import org.dabbot.discord.QueueSong

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class ReorderCommand: Command(Permission.MOD, "reorder", "order", "reordr", "changeorder", "swap") {
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
        launch(CommonPool) {
            val songs = ctx.server.queue!!.list()
            if (songs.isEmpty) {
                ctx.reply("Song queue is empty!")
                return@launch
            }
            var i = 0
            var song: QueueSong? = null
            for (s in songs) {
                if (i == currentPosition - 1) {
                    song = s
                    break
                }
                i++
            }
            if (song == null) {
                ctx.reply("Invalid song!")
                return@launch
            }
            ctx.server.queue!!.move(song, newPosition - 1)
            ctx.reply("Song moved!")
        }
    }
}