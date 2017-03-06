package ovh.not.dabbot.command

import ovh.not.dabbot.Command

class DebugCommand: Command("debug") {
    override fun on(ctx: Context) {
        var r = String()
        r += "```"
        r += "\nctx.server.guild.id: ${ctx.server.guild.id}"
        r += "\nctx.shard.id: ${ctx.shard.id}"
        r += "\nctx.server.jda!!.ping: ${ctx.shard.jda!!.ping}"
        r += "\nctx.server.connected: ${ctx.server.connected}"
        r += "\nctx.server.audioPlayer.isPaused: ${ctx.server.audioPlayer.isPaused}"
        r += "\nctx.server.audioPlayer.playingTrack: ${ctx.server.audioPlayer.playingTrack}"
        if (ctx.server.audioPlayer.playingTrack != null) {
            r += "\nctx.server.audioPlayer.playingTrack.sourceManager.sourceName: ${ctx.server.audioPlayer.playingTrack.sourceManager.sourceName}"
            r += "\nctx.server.audioPlayer.playingTrack.identifier: ${ctx.server.audioPlayer.playingTrack.identifier}"
            r += "\nctx.server.audioPlayer.playingTrack.info.title: ${ctx.server.audioPlayer.playingTrack.info.title}"
            r += "\nctx.server.audioPlayer.playingTrack.info.author: ${ctx.server.audioPlayer.playingTrack.info.author}"
            r += "\nctx.server.audioPlayer.playingTrack.info.length: ${ctx.server.audioPlayer.playingTrack.info.length}"
            ctx.server.queue!!.current { current ->
                r += "\nctx.server.queue!!.current { current ->"
                r += "\n  current?.id: ${current?.id}"
                r += "\n  current?.addedBy: ${current?.addedBy}"
                r += "\n  current?.dateAdded: ${current?.dateAdded.toString()}"
                r += "\n}"
                ctx.reply("$r```")
            }
        } else {
            ctx.reply("$r```")
        }
    }
}
