package org.dabbot.discord.command

import com.google.common.base.CharMatcher
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.dabbot.discord.Command
import org.dabbot.discord.Permission
import org.dabbot.discord.property.Prefix

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class PrefixCommand: Command(Permission.ADMIN, "prefix", "trigger", "prfix", "triger") {
    private var defaultPrefix: String? = null

    override fun on(ctx: Context) {
        if (defaultPrefix == null) {
            defaultPrefix = ctx.shard.prefix
        }
        val property = ctx.server.properties["prefix"] as Prefix
        if (ctx.args.isEmpty()) {
            launch(CommonPool) {
                ctx.reply("Command prefix: `%prefix%`\n**To change this server's prefix**, use `%prefix%prefix <new prefix>`.")
            }
        } else {
            val newPrefix = ctx.args[0]
            if (newPrefix.length > 30 || !CharMatcher.ascii().matchesAllOf(newPrefix)) {
                ctx.reply("Sorry, that prefix is invalid! We only accept ASCII letters and the prefix must not be longer than 30 letters in total.")
                return
            }
            launch(CommonPool) {
                val oldPrefix = property.getPrefixOrElse(defaultPrefix!!)
                property.setPrefix(newPrefix)
                ctx.reply("Updated this server's prefix from `$oldPrefix` to `$newPrefix`!")
            }
        }
    }
}