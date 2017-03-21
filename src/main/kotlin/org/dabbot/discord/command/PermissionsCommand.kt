package org.dabbot.discord.command

import org.dabbot.discord.Command
import org.dabbot.discord.Permission

class PermissionsCommand: Command(Permission.PERMISSIONS, "permissions", "roles", "perms", "permisions", "permission", "permissios") {
    val msg = "**dabBot's permission system**" +
            "\nLimits users who can manage song queue you need to setup dabBot's permission system." +
            "\n\n**Setup stages:**" +
            "\n1) Create a role called `dabbot-dj`." +
            "\n2) Give the `dabbot-dj` role to users who should be DJs on your server." +
            "\n\nTo disable the permissions system, simply delete the `dabbot-dj` role!"

    override fun on(ctx: Context) = ctx.reply(msg)
}