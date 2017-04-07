package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.dabbot.discord.Command
import org.dabbot.discord.Permission
import org.dabbot.discord.QueueSong

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class RadioCommand: Command(Permission.RADIO, "radio", "station", "stations", "rdio", "statins", "live") {
    override fun on(ctx: Context) {
        val stations = ctx.server.radioStations
        launch(CommonPool) {
            if (ctx.args.isEmpty()) {
                val builder = StringBuilder("Streams a variety of radio stations.\nUsage: `!!!radio <station name>`\n\n**Available stations:**\n")
                val iterator = stations.getStations(null, null, null).iterator()
                while (iterator.hasNext()) {
                    val station = iterator.next()
                    builder.append(station.title)
                    if (iterator.hasNext()) {
                        builder.append(", ")
                    }
                }
                builder.append("\n\nNeed another station? Join the support server with the link in `!!!support`.")
                ctx.reply(builder.toString())
            } else {
                val station = stations.getStations(null, null, ctx.args[0]).receiveOrNull()
                if (station == null) {
                    ctx.reply("Invalid station! For usage & stations use `!!!radio`.")
                    return@launch
                }
                station.load(ctx.server.playerManager)
                val song = QueueSong(station.track!!, ctx.event.author.id)
                ctx.server.queue!!.add(song)
            }
        }
    }
}