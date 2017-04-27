package org.dabbot.discord.command

import org.dabbot.discord.Command
import org.dabbot.discord.Permission
import java.time.Duration
import java.util.regex.Pattern

class SeekCommand: Command(Permission.MOD, "seek", "jump", "position", "positon") {
    val timePattern: Pattern = Pattern.compile("(?:(?<hours>\\d{1,2}):)?(?:(?<minutes>\\d{1,2}):)?(?<seconds>\\d{1,2})")
    val usage = "Usage: `%prefix%jump <time>`\nExample: `%prefix%jump 03:51` - starts playing the current song at 3 min 51s " +
            "instead of at the start.\nTime format: `hh:mm:ss`, e.g. 01:25:51 = 1 hour, 25 minutes & 51 seconds"

    override fun on(ctx: Context) {
        if (!ctx.server.connected || !ctx.server.playing) {
            ctx.reply("No music is playing!")
            return
        }
        if (ctx.args.isEmpty()) {
            ctx.reply(usage)
            return
        }
        val matcher = timePattern.matcher(ctx.args[0])
        if (!matcher.find()) {
            ctx.reply(usage)
            return
        }
        var sHours = matcher.group("hours")
        var sMinutes = matcher.group("minutes")
        if (sMinutes == null && sHours != null) {
            sMinutes = sHours
            sHours = null
        }
        val sSeconds = matcher.group("seconds")
        var hours: Long = 0
        var minutes: Long = 0
        var seconds: Long = 0
        try {
            if (sHours != null) {
                hours = sHours.toLong()
            }
            if (sMinutes != null) {
                minutes = sMinutes.toLong()
            }
            if (sSeconds != null) {
                seconds = sSeconds.toLong()
            }
        } catch (e: NumberFormatException) {
            ctx.reply(usage)
            return
        }
        var time: Long = Duration.ofHours(hours).toMillis()
        time += Duration.ofMinutes(minutes).toMillis()
        time += Duration.ofSeconds(seconds).toMillis()
        ctx.server.audioPlayer.playingTrack.position = time
        ctx.reply("Jumped to the specified position. Use `%prefix%nowplaying` to see the current song & position.")
    }
}