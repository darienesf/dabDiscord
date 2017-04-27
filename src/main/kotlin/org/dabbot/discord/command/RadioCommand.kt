package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.dabbot.discord.Command
import org.dabbot.discord.Permission
import org.dabbot.discord.QueueSong
import org.dabbot.discord.RadioStations

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class RadioCommand: Command(Permission.RADIO, "radio", "station", "stations", "rdio", "statins", "live") {
    private suspend fun list(stations: RadioStations, country: String?, genre: String?): String {
        val builder = StringBuilder("Streams a variety of radio stations.")
        builder.append("\nTo play a radio station: `%prefix%radio <station name>`")
        if (country == null) builder.append("\nFilter stations by country: `%prefix%radio country <country code>`")
        if (genre == null) builder.append("\nFiler stations by genre: `%prefix%radio genre <genre>`")
        builder.append("\n\n**Available stations:**\n")
        val iterator = stations.getStations(country, genre, null).iterator()
        while (iterator.hasNext()) {
            val station = iterator.next()
            builder.append(station.title)
            if (iterator.hasNext()) {
                builder.append(", ")
            }
        }
        return builder.append("\n\nNeed another station? Join the support server with the link in `%prefix%support`.").toString()
    }

    override fun on(ctx: Context) {
        val stations = ctx.server.radioStations
        launch(CommonPool) {
            if (ctx.args.isEmpty()) {
                ctx.reply(list(stations, null, null))
                return@launch
            }
            if (ctx.args.size != 2) {
                val station = stations.getStations(null, null, ctx.args[0]).receiveOrNull()
                if (station == null) {
                    ctx.reply("Invalid station! For usage & stations use `%prefix%radio`.")
                    return@launch
                }
                station.load(ctx.server.playerManager)
                val song = QueueSong(station.track!!, ctx.event.author.id)
                ctx.server.queue!!.add(song)
                return@launch
            }
            when (ctx.args[0].toLowerCase()) {
                "country" -> {
                    ctx.reply(list(stations, ctx.args[1], null))
                }
                "genre" -> {
                    ctx.reply(list(stations, null, ctx.args[1]))
                }
                else -> {
                    ctx.reply("Invalid arguments! For usage & stations use `%prefix%radio`.")
                }
            }
        }
    }
}