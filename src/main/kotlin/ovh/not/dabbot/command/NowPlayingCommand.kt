package ovh.not.dabbot.command

import ovh.not.dabbot.Command

class NowPlayingCommand: Command("nowplaying", "now", "current", "song", "np", "nowplayng") {
    override fun on(ctx: Context) {
        ctx.server.queue!!.current { song ->
            if (song == null) {
                ctx.reply("No music is playing on this guild! Use `!!!play` to play a song.")
                return@current
            }
            ctx.reply("Currently playing **${song.title}** by **${song.author}** `[${song.formatDuration(ctx.server.audioPlayer.playingTrack.position, "mm:ss")}/${song.getFormattedDuration()}]`")
        }
    }
}