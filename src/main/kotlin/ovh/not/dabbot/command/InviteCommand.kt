package ovh.not.dabbot.command

import com.mashape.unirest.http.Unirest
import net.dv8tion.jda.core.Permission
import ovh.not.dabbot.Command

class InviteCommand: Command("invite", "addbot", "add", "join", "invit") {
    val applicationsUrl = "https://discordapp.com/api/oauth2/applications/@me"
    val perms = Permission.getRaw(
            Permission.MESSAGE_READ,
            Permission.MESSAGE_WRITE,
            Permission.MESSAGE_EMBED_LINKS,
            Permission.MESSAGE_ATTACH_FILES,
            Permission.MESSAGE_ADD_REACTION,
            Permission.VOICE_CONNECT,
            Permission.VOICE_SPEAK,
            Permission.VOICE_USE_VAD
    )
    var clientId: String? = null

    override fun on(ctx: Context) {
        if (clientId == null) {
            val msg = ctx.event.channel.sendMessage("Loading bot info...").complete()
            val response = Unirest.get(applicationsUrl).header("Authorization", ctx.event.jda.token).asJson()
            if (response.status != 200) {
                msg.editMessage("Error loading bot info!\nStatus: ${response.status}\nMessage: ${response.statusText}")
                        .queue()
                return
            }
            clientId = response.body.`object`.getString("id")
            msg.delete().queue()
        }
        ctx.reply("Invite dabBot: https://discordapp.com/oauth2/authorize?client_id=$clientId&scope=bot&permissions=$perms")
    }
}