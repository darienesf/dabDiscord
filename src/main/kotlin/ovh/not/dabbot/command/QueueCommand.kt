package ovh.not.dabbot.command

import ovh.not.dabbot.Command
import ovh.not.dabbot.Pageable

class QueueCommand: Command("queue", "q", "list", "show", "l") {
    override fun on(ctx: Context) {
        ctx.server.queue!!.list { songs ->
            ctx.server.queue!!.current { song ->
                var r = String()
                if (song != null) {
                    r += "__Currently playing:__"
                    r += "\n${song.title} by ${song.author} `[${song.getFormattedDuration()}]`\n\n"
                }
                if (songs == null) {
                    if (r.isEmpty()) {
                        ctx.reply("The song queue is empty and no song is currently playing! Add a song with `!!!play <song name or title>`.")
                    } else {
                        ctx.reply(r)
                    }
                    return@current
                }
                val pageable = Pageable(songs)
                pageable.setPageSize(10)
                if (ctx.args.isNotEmpty()) {
                    val p = ctx.args[0].toIntOrNull()
                    if (p == null) {
                        ctx.reply("Invalid page! Must be an integer within the range ${pageable.getMinPageRange()} - ${pageable.getMaxPages()}")
                        return@current
                    } else {
                        pageable.setPage(p)
                    }
                } else {
                    pageable.setPage(pageable.getMinPageRange())
                }
                r += "__Song queue:__ (Page **${pageable.getPage()}** of **${pageable.getMaxPages()}**)"
                var index = 1
                pageable.getListForPage().forEach { item ->
                    r += "\n`%02d` ".format(((pageable.getPage() - 1) * pageable.getPageSize()) + index) + "${item.title} by ${item.author} `[${item.getFormattedDuration()}]`"
                    index++
                }
                if (pageable.getPage() < pageable.getMaxPages()) {
                    r += "\n\n__To see the next page:__ `!!!queue ${pageable.getPage() + 1}`\nTo see the full queue, use `!!!queue all`"
                }
                ctx.reply(r)
            }
        }
    }
}