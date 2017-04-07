package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.dabbot.discord.Command
import org.dabbot.discord.Permission

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class DebugCommand: Command(Permission.DEBUG, "debug") {
    override fun on(ctx: Context) {
        launch(CommonPool) {
            var r = String()
            r += "```"
            r += "\nguild.id: ${ctx.server.guild.id}"
            r += "\nshard.id: ${ctx.shard.shard}"
            r += "\nping: ${ctx.shard.jda!!.ping}"
            r += "\nconnected: ${ctx.server.connected}"
            r += "\nisPaused: ${ctx.server.audioPlayer.isPaused}"
            r += "\nplayingTrack: ${ctx.server.audioPlayer.playingTrack}"
            if (ctx.server.audioPlayer.playingTrack != null) {
                r += "\nsourceName: ${ctx.server.audioPlayer.playingTrack.sourceManager.sourceName}"
                r += "\nidentifier: ${ctx.server.audioPlayer.playingTrack.identifier}"
                r += "\ntitle: ${ctx.server.audioPlayer.playingTrack.info.title}"
                r += "\nauthor: ${ctx.server.audioPlayer.playingTrack.info.author}"
                r += "\nlength: ${ctx.server.audioPlayer.playingTrack.info.length}"
                val current = ctx.server.queue!!.current()
                r += "\ncurrent queueSongId: ${current?.queueSongId}"
                r += "\ncurrent songId: ${current?.songId}"
                r += "\ncurrent addedBy: ${current?.addedBy}"
                r += "\ncurrent dateAdded: ${current?.dateAdded.toString()}"
            }
            ctx.reply("$r```")
        }
    }
}
