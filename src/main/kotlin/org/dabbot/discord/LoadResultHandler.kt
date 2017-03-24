package org.dabbot.discord

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
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
        launch(CommonPool) {
            ctx.server.queue?.add(song)
        }
    }

    override fun trackLoaded(t: AudioTrack) {
        loadTrack(t)
        if (!ctx.server.playing) {
            launch(CommonPool) {
                ctx.server.play(ctx.server.queue!!.next()!!)
            }
        }
    }

    override fun playlistLoaded(p: AudioPlaylist) {
        if (p.selectedTrack != null) {
            trackLoaded(p.selectedTrack)
        } else if (p.isSearchResult) {
            val list = ArrayList<SelectorItem>()
            p.tracks.forEach { t ->
                val song = QueueSong(t.info.title, t.info.author, t.duration, t)
                list.add(song)
            }
            val selector = Selector(list, { found, item ->
                ctx.server.selectors.remove(ctx.event.author)
                if (!found) {
                    ctx.reply("Selection cancelled!")
                    return@Selector
                }
                if (item is Song) {
                    trackLoaded(item.track!!)
                }
            }, 5)
            ctx.server.selectors[ctx.event.author] = selector
            ctx.reply(selector.display())
        } else {
            p.tracks.forEach { t -> loadTrack(t) }
            // todo added %d songs to queue
        }
    }

    class SongQuery(val name: String, var search: Boolean)
}