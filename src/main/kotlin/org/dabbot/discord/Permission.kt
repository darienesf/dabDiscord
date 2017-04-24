package org.dabbot.discord

import java.util.*

enum class Permission {
    INFO, QUEUE, PLAY, RADIO, DISCORD_FM, MOD, ADMIN;
    val bits = 1 shl ordinal
}

private fun get(vararg permissions: Permission): Int {
    var value = 0
    permissions.forEach { permission ->
        value = value or permission.bits
    }
    return value
}

fun listPermissions(bits: Int): List<Permission> {
    val list = LinkedList<Permission>()
    Permission.values().forEach {
        if ((bits and it.bits) == it.bits) {
            list.add(it)
        }
    }
    return list
}

val defaultPermissions = get(Permission.INFO, Permission.QUEUE, Permission.PLAY, Permission.RADIO, Permission.DISCORD_FM, Permission.MOD)
val administratorPermissions = defaultPermissions or Permission.ADMIN.bits
