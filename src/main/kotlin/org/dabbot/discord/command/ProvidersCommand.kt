package org.dabbot.discord.command

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager
import org.dabbot.discord.Command
import org.dabbot.discord.Permission

class ProvidersCommand: Command(Permission.ABOUT, "providers", "sources", "provider", "source", "sorces", "provides") {
    private var msg: String? = null

    private fun init(manager: DefaultAudioPlayerManager) {
        val field = manager::class.java.getDeclaredField("sourceManagers")
        field.isAccessible = true
        val value = field.get(manager)
        val list = (value as List<*>).iterator()
        val builder = StringBuilder("Available music providers: ")
        while (list.hasNext()) {
            val source = list.next() as AudioSourceManager
            builder.append(source.sourceName.toLowerCase())
            if (list.hasNext()) {
                builder.append(", ")
            }
        }
        msg = builder.toString()
    }

    override fun on(ctx: Context) {
        if (msg == null) {
            init(ctx.server.playerManager as DefaultAudioPlayerManager)
        }
        ctx.reply(msg!!)
    }
}