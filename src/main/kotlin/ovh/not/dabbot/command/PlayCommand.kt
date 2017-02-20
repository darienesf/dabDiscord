package ovh.not.dabbot.command

import ovh.not.dabbot.Command
import ovh.not.dabbot.LoadResultHandler

class PlayCommand: Command("play", "p", "search", "yt", "youtube") {
    override fun on(ctx: Context) {
        if (ctx.args.isEmpty()) {
            ctx.reply("usage msg lol")
            return
        }
        if (!ctx.isUserInVoiceChannel()) {
            ctx.reply("You must be in a voice channel!")
            return
        }
        if (!ctx.server.connected) {
            ctx.server.open(ctx.getUserVoiceChannel()!!)
        }
        val query = ctx.args.joinToString(" ")
        ctx.server.playerManager.loadItem(query, LoadResultHandler(ctx, LoadResultHandler.SongQuery(query, false)))
    }
}