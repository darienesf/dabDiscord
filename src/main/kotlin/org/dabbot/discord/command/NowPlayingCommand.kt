package org.dabbot.discord.command

import org.dabbot.discord.Command
import org.dabbot.discord.Permission

class NowPlayingCommand: Command(Permission.NOW_PLAYING, "nowplaying", "now", "current", "song", "np", "nowplayng") {
    override fun on(ctx: Context) {
        val song = ctx.server.queue!!.current()
        if (song == null) {
            ctx.reply("No music is playing on this guild! Use `!!!play` to play a song.")
            return
        }
        ctx.reply("Currently playing **${song.title}** by **${song.author}** `[${song.formatDuration(ctx.server.audioPlayer.playingTrack.position, "mm:ss")}/${song.getFormattedDuration()}]`")
    }
}