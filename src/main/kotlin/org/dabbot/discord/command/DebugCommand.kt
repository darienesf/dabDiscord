package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.dabbot.discord.Command
import org.dabbot.discord.Permission

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class DebugCommand: Command(Permission.DEBUG, "debug") {
    override fun on(ctx: Context) {
        launch(CommonPool) {
            val builder = StringBuilder()
            builder.append("__Shard:__")
            builder.append("\nID: ${ctx.event.jda.shardInfo.shardId}/${ctx.event.jda.shardInfo.shardTotal}")
            builder.append("\nPing: ${ctx.event.jda.ping}")
            builder.append("\n\n__Guild:__")
            builder.append("\nID: `${ctx.event.guild.id}`")
            builder.append("\nConnected: ${ctx.server.connected}")
            builder.append("\nPaused: ${ctx.server.audioPlayer.isPaused}")
            builder.append("\n\n__User:__")
            builder.append("\nID: `${ctx.event.author.id}`")
            val track = ctx.server.audioPlayer.playingTrack
            if (track != null) {
                val current = ctx.server.queue!!.current()
                builder.append("\n\n__Song:__")
                builder.append("\nID: `${current?.songId}`")
                builder.append("\nSource: ${track.sourceManager.sourceName}")
                builder.append("\nIdentifier: ${track.identifier}")
                builder.append("\nTitle: ${track.info.title}")
                builder.append("\nAuthor: ${track.info.author}")
                builder.append("\nDuration: ${current?.getFormattedDuration()}")
                builder.append("\nTrack: $track")
                builder.append("\n\n__Queue song:__")
                builder.append("\nID: `${current?.queueSongId}`")
                builder.append("\nAdded by: `${current?.addedBy}`")
                builder.append("\nDate added: ${current?.dateAdded.toString()}")
            }
            ctx.reply(builder.toString())
        }
    }
}
