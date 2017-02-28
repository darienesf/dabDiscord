package ovh.not.dabbot.command

import ovh.not.dabbot.Command

class HelpCommand: Command("help", "commands", "h", "music", "cmds", "hlp") {
    override fun on(ctx: Context) {
        throw UnsupportedOperationException()
    }
}