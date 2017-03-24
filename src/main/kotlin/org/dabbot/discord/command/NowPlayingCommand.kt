package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.dabbot.discord.Command
import org.dabbot.discord.Permission

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class NowPlayingCommand: Command(Permission.NOW_PLAYING, "nowplaying", "now", "current", "song", "np", "nowplayng") {
    override fun on(ctx: Context) {
        launch(CommonPool) {
            val song = ctx.server.queue!!.current()
            if (song == null) {
                ctx.reply("No music is playing on this guild! Use `!!!play` to play a song.")
                return@launch
            }
            ctx.reply("Currently playing **${song.title}** by **${song.author}** `[${song.formatDuration(ctx.server.audioPlayer.playingTrack.position, "mm:ss")}/${song.getFormattedDuration()}]`")

        }
    }
}