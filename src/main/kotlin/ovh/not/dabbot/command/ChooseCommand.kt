package ovh.not.dabbot.command

import ovh.not.dabbot.Command

class ChooseCommand: Command("choose", "pick", "select", "cancel", "c", "choos", "chose") {
    override fun on(ctx: Context) {
        throw UnsupportedOperationException()
    }
}