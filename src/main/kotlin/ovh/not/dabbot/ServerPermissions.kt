package ovh.not.dabbot

import org.json.JSONObject

class ServerPermissions(val requester: Requester, val server: Server) {
    fun invalidateCaches() {
    }

    fun list(callback: (Array<Role?>?) -> Unit) {
        val r = requester.execute(Method.GET, "/permissions/${server.guild.id}")
        if (r.code() != 200) {
            r.close()
            callback(null)
            return
        }
        val json = JSONObject(r.body().string())
        r.close()
        if (json.isNull("error")) {
            val j = json.getJSONArray("permissions")
            val a = arrayOfNulls<Role>(j.length())
            var index = 0
            j.forEach { i ->
                if (i is JSONObject) {
                    a[index] = Role(i.getString("role"), i.getInt("value"))
                }
                index++
            }
            callback(a)
        } else {
            callback(null)
            println("permissions#list error: ${json.getString("error")}")
        }
    }

    fun get(role: String, callback: (Role?) -> Unit) {
        val r = requester.execute(Method.GET, "/permissions/${server.guild.id}/$role")
        if (r.code() != 200) {
            r.close()
            callback(null)
            return
        }
        val json = JSONObject(r.body().string())
        r.close()
        if (json.isNull("error")) {
            val perms = json.getJSONObject("permissions")
            callback(Role(perms.getString("role"), perms.getInt("value")))
        } else {
            callback(null)
            println("permissions#get error $role: ${json.getString("error")}")
        }
    }

    fun add(role: String, value: Int, callback: () -> Unit) {
        val r = requester.executePlainText(Method.POST, "/permissions/${server.guild.id}/$role", value.toString())
        if (r.code() != 200) {
            val json = JSONObject(r.body().string())
            r.close()
            if (!json.isNull("error")) {
                println("permissions#add error $role: ${json.getString("error")}")
            }
            callback()
            return
        }
        r.close()
        callback()
    }

    fun update(role: String, value: Int, callback: () -> Unit) {
        val r = requester.executePlainText(Method.PUT, "/permissions/${server.guild.id}/$role", value.toString())
        if (r.code() != 200) {
            val json = JSONObject(r.body().string())
            r.close()
            if (!json.isNull("error")) {
                println("permissions#update error $role: ${json.getString("error")}")
            }
            callback()
            return
        }
        r.close()
        callback()
    }

    fun update(role: Role, callback: () -> Unit) = update(role.role, role.value, callback)

    fun delete(role: String, callback: () -> Unit) {
        val r = requester.execute(Method.DELETE, "/permissions/${server.guild.id}/$role")
        if (r.code() != 200) {
            val json = JSONObject(r.body().string())
            r.close()
            if (!json.isNull("error")) {
                println("permissions#delete error $role: ${json.getString("error")}")
            }
            callback()
            return
        }
        r.close()
        callback()
    }

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
        ROLES           (1 shl 18),
        ANNOUNCEMENTS   (1 shl 19),

        GROUP_INFO(ABOUT.bits or HELP.bits or INVITE.bits or DEBUG.bits),
        GROUP_CURRENT(NOW_PLAYING.bits or QUEUE.bits),
        GROUP_PLAY(PLAY.bits),
        GROUP_RADIO(DISCORD_FM.bits or RADIO.bits),
        GROUP_MODERATION(PAUSE.bits or RESUME.bits or SEEK.bits or RESTART.bits or SKIP.bits or CLEAR.bits
                or SHUFFLE.bits or REORDER.bits or REPEAT.bits or ROLES.bits or ANNOUNCEMENTS.bits)
    }

    class Role(val role: String, var value: Int) {
        fun has(perm: Permission): Boolean {
            return (value and perm.bits) == perm.bits
        }

        fun set(perm: Permission) {
            value = value or perm.bits
        }

        fun unset(perm: Permission) {
            value = value and perm.bits.inv()
        }
    }
}