package org.dabbot.discord

import net.dv8tion.jda.core.entities.Member

val everyonePermissions = get(
        Permission.ABOUT, Permission.HELP, Permission.INVITE, Permission.DEBUG, Permission.NOW_PLAYING,
        Permission.QUEUE, Permission.PLAY
)
val djPermissions = get(
        Permission.DISCORD_FM, Permission.RADIO,       Permission.PAUSE,        Permission.RESUME,  Permission.SEEK,
        Permission.RESTART,    Permission.SKIP,        Permission.CLEAR,        Permission.SHUFFLE, Permission.REORDER,
        Permission.REPEAT
)

enum class Permission(val bits: Int) {
    ABOUT           (1 shl 0),//info
    HELP            (1 shl 1),
    INVITE          (1 shl 2),
    DEBUG           (1 shl 3),
    NOW_PLAYING     (1 shl 4),//current
    QUEUE           (1 shl 5),
    PLAY            (1 shl 6),//play
    DISCORD_FM      (1 shl 7),//radio
    RADIO           (1 shl 8),
    PAUSE           (1 shl 9),//moderation
    RESUME          (1 shl 10),
    SEEK            (1 shl 11),
    RESTART         (1 shl 12),
    SKIP            (1 shl 13),
    CLEAR           (1 shl 14),
    SHUFFLE         (1 shl 15),
    REORDER         (1 shl 16),
    REPEAT          (1 shl 17),
    PERMISSIONS     (1 shl 18),
    ANNOUNCEMENTS   (1 shl 19),
    ADMIN           (1 shl 20),
    IGNORE          (1 shl 21),
    STOP            (1 shl 22),
    REMOVE          (1 shl 23),
}

fun get(vararg permissions: Permission): Int {
    var value = 0
    permissions.forEach { permission ->
        value = value or permission.bits
    }
    return value
}

fun hasPermission(member: Member, permission: Permission): Boolean {
    // administrator overrides any other permission setup
    if (member.hasPermission(net.dv8tion.jda.core.Permission.ADMINISTRATOR)) {
        return true
    }
    // if everyone has the permission
    if ((everyonePermissions and permission.bits) == permission.bits) {
        return true
    }
    val roles = member.guild.getRolesByName("dabbot-dj", true)
    if (roles == null || roles.isEmpty()) {
        // permissions aren't setup, assume everyone is a dj
        return (djPermissions and permission.bits) == permission.bits
    }
    if (member.roles.contains(roles[0])) {
        // permissions are setup and user has dj role
        return (djPermissions and permission.bits) == permission.bits
    }
    return false
}