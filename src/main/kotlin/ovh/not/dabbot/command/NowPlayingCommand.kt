package ovh.not.dabbot.command

import ovh.not.dabbot.Command

class NowPlayingCommand: Command("nowplaying", "now", "current", "song", "np", "nowplayng") {
    override fun on(ctx: Context) {
        throw UnsupportedOperationException()
    }
}