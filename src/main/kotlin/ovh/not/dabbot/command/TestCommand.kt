package ovh.not.dabbot.command

import ovh.not.dabbot.Command

class TestCommand: Command("test", "t") {
    override fun on(ctx: Context) {
        ctx.reply("Testing, 1 2 3!")
    }
}

