package ovh.not.dabbot.command

import ovh.not.dabbot.Command

class LoopCommand: Command("loop", "cycle", "lop", "cicle", "cycl") {
    override fun on(ctx: Context) {
        throw UnsupportedOperationException()
    }
}