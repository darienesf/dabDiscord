package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Role
import org.dabbot.discord.Command
import org.dabbot.discord.Permission
import org.dabbot.discord.defaultPermissions
import org.dabbot.discord.listPermissions

@Suppress("NON_EXHAUSTIVE_WHEN", "EXPERIMENTAL_FEATURE_WARNING")
class PermissionsCommand: Command(Permission.ADMIN, "permissions", "roles", "perms", "permisions", "permission", "permissios") {
    private fun equalsAny(test: String, vararg cases: String): Boolean {
        cases.forEach {
            if (it.equals(test, true)) {
                return true
            }
        }
        return false
    }

    private fun permissionFromInput(input: String): Permission? {
        Permission.values().forEach {
            if (it.name.replace("_", "").equals(input.replace("_", "").replace(" ", ""), true)) {
                return it
            }
            when (it) {
                Permission.DISCORD_FM -> if (equalsAny(input, "dfm", "discord.fm")) return it
                Permission.INFO -> if (input.equals("about", true)) return it
                Permission.PLAY -> if (input.equals("add", true)) return it
                Permission.MOD -> if (equalsAny(input, "mods", "moderation", "manage", "moderate")) return it
                Permission.ADMIN -> if (equalsAny(input, "admins", "administration", "administrate")) return it
            }
        }
        return null
    }

    private fun roleFromInput(guild: Guild, input: String): Role? {
        if (equalsAny(input, "everyone", "@everyone", "*")) {
            return guild.publicRole
        }
        guild.roles.forEach {
            if (it.name.equals(input, true) || it.id == input) {
                return it
            }
        }
        return null
    }

    private fun permsToString(iterator: Iterator<Permission>): String {
        val builder = StringBuilder()
        iterator.forEach {
            builder.append(it.name.toLowerCase().replace("_", ""))
            if (iterator.hasNext()) {
                builder.append(", ")
            }
        }
        return builder.toString()
    }

    private fun permsToStringWithCmds(): String {
        val cmds = HashMap<Permission, HashSet<Command>>(Permission.values().size)
        manager!!.commands.values.forEach {
            if (it.permission != null) {
                val list: HashSet<Command>
                if (!cmds.contains(it.permission)) {
                    list = HashSet()
                    cmds[it.permission] = list
                } else {
                    list = cmds[it.permission]!!
                }
                list.add(it)
            }
        }
        val builder = StringBuilder()
        val iterator = cmds.iterator()
        iterator.forEach { entry ->
            builder.append("`").append(entry.key.name.toLowerCase().replace("_", "")).append("`: ")
            val cmdIterator = entry.value.iterator()
            cmdIterator.forEach {
                builder.append(it.names[0])
                if (cmdIterator.hasNext()) {
                    builder.append(", ")
                }
            }
            if (iterator.hasNext()) {
                builder.append("\n")
            }
        }
        return builder.toString()
    }

    private var usage: String? = null

    private fun getUsage(): String {
        if (usage != null) return usage!!
        usage = "dabBot's permission system allows control over which commands each role can use!\n\n" +
                "Commands:" +
                "\n`!!!perms` - shows this message." +
                "\n`!!!perms view <role>` - view the permissions of the specified role." +
                "\n`!!!perms allow <role> <permission>` - allow a role to use commands within the specified permission." +
                "\n`!!!perms deny <role> <permission>` - deny a role from using commands within the specified permission." +
                "\n`!!!perms reset <role>` - reset the permissions of a role.\n\n" +
                "Hierarchy:" +
                "\nThe dabBot permission system uses the base permissions of the `everyone` role and any other roles are added to the permissions of the `everyone` role." +
                "\nAn important factor to take into account is that you cannot deny a permission from a role that the `everyone` role has.\n\n" +
                "Available permissions with allowed commands:" +
                "\n" + permsToStringWithCmds()
        return usage!!
    }

    private fun getRoleName(role: Role): String {
        if (role.id == role.guild.id) {
            return "everyone"
        }
        return role.name
    }

    override fun on(ctx: Context) {
        if (ctx.args.isEmpty()) {
            val usage = getUsage()
            println(usage)
            ctx.reply(usage)
            return
        }
        val perms = ctx.server.permissions
        when (ctx.args[0].toLowerCase()) {
            "view" -> {
                if (ctx.args.size < 2) {
                    ctx.reply(getUsage())
                    return
                }
                val role = roleFromInput(ctx.event.guild, ctx.args[1])
                if (role == null) {
                    ctx.reply("Could not find the role `${ctx.args[1]}`. Valid inputs: role name, role id, everyone, *")
                    return
                }
                launch(CommonPool) {
                    var bits = perms.getRole(role.id)
                    if (bits == null) {
                        if (role.id == ctx.event.guild.id) {
                            bits = defaultPermissions
                        } else {
                            ctx.reply("Role `${getRoleName(role)}` has no permissions setup!")
                            return@launch
                        }
                    }
                    ctx.reply("${getRoleName(role)}'s permissions: ${permsToString(listPermissions(bits).iterator())}")
                }
            }
            "allow" -> {
                if (ctx.args.size < 3) {
                    ctx.reply(getUsage())
                    return
                }
                val role = roleFromInput(ctx.event.guild, ctx.args[1])
                if (role == null) {
                    ctx.reply("Could not find the role `${ctx.args[1]}`. Valid inputs: role name, role id, everyone, *")
                    return
                }
                val permission = permissionFromInput(ctx.args[2])
                if (permission == null) {
                    ctx.reply("Invalid permission node `${ctx.args[2]}`. See `!!!perms` for a list of available permission nodes!")
                    return
                }
                launch(CommonPool) {
                    var bits = perms.getRole(role.id)
                    var exists = true
                    if (bits == null) {
                        exists = false
                        if (role.id == ctx.event.guild.id) {
                            bits = defaultPermissions
                        } else {
                            bits = 0
                        }
                    }
                    bits = bits!! or permission.bits
                    if (exists) {
                        perms.updateRole(role.id, bits!!)
                    } else {
                        perms.add(role.id, bits!!)
                    }
                    bits = perms.getRole(role.id)
                    ctx.reply("Updated ${getRoleName(role)}'s permissions: ${permsToString(listPermissions(bits!!).iterator())}")
                }
            }
            "deny" -> {
                if (ctx.args.size < 3) {
                    ctx.reply(getUsage())
                    return
                }
                val role = roleFromInput(ctx.event.guild, ctx.args[1])
                if (role == null) {
                    ctx.reply("Could not find the role `${ctx.args[1]}`. Valid inputs: role name, role id, everyone, *")
                    return
                }
                val permission = permissionFromInput(ctx.args[2])
                if (permission == null) {
                    ctx.reply("Invalid permission node `${ctx.args[2]}`. See `!!!perms` for a list of available permission nodes!")
                    return
                }
                launch(CommonPool) {
                    var bits = perms.getRole(role.id)
                    var exists = true
                    if (bits == null) {
                        exists = false
                        if (role.id == ctx.event.guild.id) {
                            bits = defaultPermissions
                        } else {
                            bits = 0
                        }
                    }
                    bits = bits!! and permission.bits.inv()
                    if (exists) {
                        perms.updateRole(role.id, bits!!)
                    } else {
                        perms.add(role.id, bits!!)
                    }
                    bits = perms.getRole(role.id)
                    ctx.reply("Updated ${getRoleName(role)}'s permissions: ${permsToString(listPermissions(bits!!).iterator())}")
                }
            }
            "reset" -> {
                if (ctx.args.size < 2) {
                    ctx.reply(getUsage())
                    return
                }
                val role = roleFromInput(ctx.event.guild, ctx.args[1])
                if (role == null) {
                    ctx.reply("Could not find the role `${ctx.args[1]}`. Valid inputs: role name, role id, everyone, *")
                    return
                }
                launch(CommonPool) {
                    var bits = perms.getRole(role.id)
                    var exists = true
                    if (bits == null) {
                        exists = false
                    }
                    if (!exists) {
                        ctx.reply("The role `${getRoleName(role)}` has no permissions setup so it was not reset!")
                        return@launch
                    }
                    perms.deleteRole(role.id)
                    if (role.id == role.guild.id) {
                        bits = defaultPermissions
                    } else {
                        bits = 0
                    }
                    ctx.reply("Reset ${getRoleName(role)}'s permissions: ${permsToString(listPermissions(bits!!).iterator())}")
                }
            }
        }
    }
}