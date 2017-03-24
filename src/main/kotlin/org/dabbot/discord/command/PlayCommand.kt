package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.dabbot.discord.Command
import org.dabbot.discord.LoadResultHandler
import org.dabbot.discord.Permission

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
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
            launch(CommonPool) {
                ctx.server.open(ctx.getUserVoiceChannel()!!)
            }
        }
        val query = ctx.args.joinToString(" ")
        ctx.server.playerManager.loadItem(query, LoadResultHandler(ctx, LoadResultHandler.SongQuery(query, false)))
    }
}