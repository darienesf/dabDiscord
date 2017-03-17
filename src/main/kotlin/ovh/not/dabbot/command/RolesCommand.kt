package ovh.not.dabbot.command

import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Role
import ovh.not.dabbot.Command

class RolesCommand: Command("roles", "permissions", "role", "perms", "perm", "permission", "permisions", "permision") {
    override fun on(ctx: Context) {
        if (ctx.args.isEmpty()) {
            ctx.server.permissions.list { roles ->
                if (roles == null || roles.isEmpty()) {
                    ctx.reply("No roles have been setup! Enter the setup stage with `!!!roles setup`.")
                    return@list
                }
                val builder = StringBuilder("Roles: ")
                val iterator = roles.iterator()
                while (iterator.hasNext()) {
                    val role = iterator.next()!!.role
                    if (role == "0") {
                        builder.append("everyone")
                    } else {
                        val r = ctx.event.guild.getRoleById(role)
                        builder.append(r.name)
                    }
                    if (iterator.hasNext()) {
                        builder.append(", ")
                    }
                }
                builder.append("\nView a role: `!!!roles view <role name>`")
                builder.append("\nCreate a role: `!!!roles create <role name>`")
                builder.append("\nEdit a role: `!!!roles edit <role name>`")
                builder.append("\nDelete a role: `!!!roles delete <role name>`")
                builder.append("\nRestart roles setup: `!!!roles setup`")
                ctx.reply(builder.toString())
            }
            return
        }
        when (ctx.args[0].toLowerCase()) {
            "setup", "start", "s", "set" -> {

            }
            else -> {
                if (ctx.args.size > 1) {
                    when (ctx.args[0].toLowerCase()) {
                        "view", "see", "v" -> {
                            if (ctx.args.size != 2) {
                                ctx.reply("todo usage lol")
                                return
                            }
                            val arg1 = ctx.args[1].toLowerCase()
                            val roleId: String
                            if (arg1 == "everyone") {
                                roleId = "0"
                            } else {
                                val role = findRoleByName(ctx.event.guild, ctx.args[1].toLowerCase())
                                if (role == null) {
                                    ctx.reply("Role not found on this guild!")
                                    return
                                }
                                roleId = role.id
                            }
                            ctx.server.permissions.get(roleId, { r ->
                                if (r == null) {
                                    ctx.reply("Role not setup with dabBot!")
                                    return@get
                                }
                                val perms = r.permissions()
                                val builder = StringBuilder("Permissions for `")
                                if (r.role == "0") {
                                    builder.append("everyone")
                                } else {
                                    val role = ctx.event.guild.getRoleById(r.role)
                                    builder.append(role.name)
                                }
                                builder.append("`: ")
                                val iterator = perms.iterator()
                                while (iterator.hasNext()) {
                                    builder.append(iterator.next().toString().toLowerCase().replace("_", " "))
                                    if (iterator.hasNext()) {
                                        builder.append(", ")
                                    }
                                }
                                ctx.reply(builder.toString())
                            })
                            return
                        }
                        "create", "add", "new", "a" -> {
                            return
                        }
                        "edit", "change", "e", "c", "modify" -> {
                            return
                        }
                        "delete", "delet", "remove", "rm", "d" -> {
                            return
                        }
                    }
                }
                ctx.reply("todo usage lol")
            }
        }
    }

    private fun findRoleByName(guild: Guild, name: String): Role? {
        val roles = guild.getRolesByName(name, true)
        if (roles.isEmpty()) {
            return null
        } else {
            return roles[0]
        }
    }
}