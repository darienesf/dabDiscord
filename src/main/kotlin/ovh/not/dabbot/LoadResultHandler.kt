package ovh.not.dabbot

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import java.util.function.Consumer

class LoadResultHandler(val ctx: Command.Context, val query: SongQuery): AudioLoadResultHandler {
    override fun noMatches() {
        if (query.search) {
            ctx.reply("No song matches found!")
            return
        }
        query.search = true
        ctx.server.playerManager.loadItem("ytsearch: " + query.name, LoadResultHandler(ctx, query))
    }

    override fun loadFailed(e: FriendlyException) {
        e.printStackTrace()
        ctx.reply("An error occurred") // todo case specific messages
    }

    private fun loadTrack(t: AudioTrack) {
        val song = QueueSong(t, ctx.event.author.id, ctx.server.playerManager)
        ctx.server.queue?.add(song)
    }

    override fun trackLoaded(t: AudioTrack) {
        loadTrack(t)
        if (!ctx.server.playing) {
            ctx.server.queue!!.next(Consumer { s ->
                ctx.server.play(s)
            })
        }
    }

    override fun playlistLoaded(p: AudioPlaylist) {
        if (p.selectedTrack != null) {
            trackLoaded(p.selectedTrack)
        } else if (p.isSearchResult) {

        } else {
            p.tracks.forEach { t -> loadTrack(t) }
            // todo added %d songs to queue
        }
    }

    class SongQuery(val name: String, var search: Boolean)
}