package ovh.not.dabbot.command

import ovh.not.dabbot.Command

class HelpCommand: Command("help", "commands", "h", "music", "cmds", "hlp") {
    val msg = "**Commands:**" +
            "\n`about` Information about the bot" +
            "\n`help` Shows command help" +
            "\n`invite` Adds the bot to your server" +
            "\n`play` Adds a song to the queue and begins playing" +
            "\n`queue` Shows the song queue" +
            "\n`nowplaying` Shows the current song" +
            "\n`skip` Plays the next song in the queue" +
            "\n`stop` Stops playing and leaves the voice channel" +
            "\n`radio` Streams popular radio stations" +
            "\n`discordfm` Plays music from discord.fm playlists" +
            "\n`pause` Pauses the song" +
            "\n`resume` Resumes the song" +
            "\n`choose` Picks from a selection menu" +
            "\n`repeat` Loops the current song" +
            "\n`restart` Plays a song from the beginning" +
            "\n`jump` Skips to a time in the current song" +
            "\n`reorder` Changes the position of a song in the queue" +
            "\n`shuffle` Shuffles the song queue" +
            "\n`move` Moves the bot to a different voice channel" +
            "\n\n**Quick start:** Use `!!!play <link>` to start playing a song, use the same command to add another " +
            "song, `!!!skip` to go to the next song and `!!!stop` to stop playing and leave."

    override fun on(ctx: Context) {
        ctx.reply(msg)
    }
}