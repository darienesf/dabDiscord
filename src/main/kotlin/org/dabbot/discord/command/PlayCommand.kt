package org.dabbot.discord.command

import org.dabbot.discord.Command
import org.dabbot.discord.LoadResultHandler
import org.dabbot.discord.Permission

class PlayCommand: Command(Permission.PLAY, "play", "p", "search", "yt", "youtube", "lookup", "find", "sing") {
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