package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.dabbot.discord.Command
import org.dabbot.discord.Permission

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class QueueCommand : Command(Permission.QUEUE, "queue", "q", "list", "show", "l", "sq", "showqueue", "songs") {
    companion object {
        @JvmStatic val PAGE_SIZE = 10
    }

    override fun on(ctx: Context) {
        var page: Int?
        if (ctx.args.isEmpty()) {
            page = 0
        } else {
            page = ctx.args[0].toIntOrNull()
            if (page == null) {
                ctx.reply("Invalid page! Usage: `%prefix%queue <page number>`")
                return
            }
        }
        page -= 1
        if (page < 0) {
            page = 0
        }
        launch(CommonPool) {
            val builder = StringBuilder()
            val current = ctx.server.queue!!.current()
            if (current != null) {
                builder.append("__Currently playing:__")
                builder.append("\n${current.title} ")
                val formattedPosition = current.formatDuration(ctx.server.audioPlayer.playingTrack.position, "mm:ss")
                if (!current.track?.info?.isStream!!) {
                    builder.append("by ${current.author} `[$formattedPosition/${current.getFormattedDuration()}]`")
                } else {
                    builder.append("`[$formattedPosition]`")
                }
                builder.append("\n\n")
            }
            val size = ctx.server.queue!!.size()
            if (size == 0) {
                if (builder.isEmpty()) {
                    ctx.reply("The song queue is empty and no song is currently playing! Add a song with `%prefix%play <song name or title>`.")
                } else {
                    ctx.reply(builder.toString())
                }
                return@launch
            }
            val maxPage = Math.floorDiv(size, PAGE_SIZE)
            if (page!! * PAGE_SIZE > size) {
                page = maxPage
            }
            builder.append("__Song queue:__ (Page **${page!! + 1}** of **${maxPage + 1}**)")
            val offset = page!! * PAGE_SIZE
            var i = offset + 1
            for (song in ctx.server.queue!!.list(PAGE_SIZE, offset)) {
                builder.append("\n`%02d`".format(i) + " ${song.title} ")
                if (!song.track?.info?.isStream!!) {
                    builder.append("by ${song.author} `[${song.getFormattedDuration()}]`")
                }
                i++
            }
            if (page!! < maxPage) {
                builder.append("\n\n__To see the next page:__ `%prefix%queue ${page!! + 2}`\nTo see the full queue, use `%prefix%queue all`")
            }
            ctx.reply(builder.toString())
        }
    }
}