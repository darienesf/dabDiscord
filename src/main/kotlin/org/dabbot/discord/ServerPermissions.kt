package org.dabbot.discord

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.produce
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.Role
import org.json.JSONObject

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
internal class ServerPermissions(val server: Server) {
    class ServerRole(val id: String, val bits: Int) {
        constructor(json: JSONObject): this(json.getString("id"), json.getInt("bits"))
    }

    private val cache = HashMap<String, Int?>()

    internal fun invalidateCache() {
        cache.clear()
    }

    suspend fun hasPermission(role: Role, permission: Permission): Boolean {
        if (role.hasPermission(net.dv8tion.jda.core.Permission.MANAGE_SERVER)
                || role.hasPermission(net.dv8tion.jda.core.Permission.ADMINISTRATOR)) {
            return true
        }
        val bits = getRole(role.id) ?: return role.id == role.guild.id
                && (defaultPermissions and permission.bits) == permission.bits
        return (bits and permission.bits) == permission.bits
    }

    suspend fun hasPermission(member: Member, permission: Permission): Boolean {
        if (member.isOwner) {
            return true
        }
        loadCache()
        if (hasPermission(member.guild.publicRole, permission)) {
            return true
        }
        member.roles.forEach { role ->
            if (hasPermission(role, permission)) {
                return true
            }
        }
        return false
    }

    suspend fun loadCache() {
        if (!cache.isEmpty()) {
            return
        }
        val req = server.requester.execute(Method.GET, "/permissions/${server.guild.id}")
        if (req.code() != 200) {
            // todo handle
            req.close()
            return
        }
        val json = JSONObject(req.body().string()).getJSONArray("roles")
        req.close()
        json.map { ServerRole(it as JSONObject) }.forEach {
            cache[it.id] = it.bits
        }
    }

    suspend fun list() = produce(CommonPool) {
        val req = server.requester.execute(Method.GET, "/permissions/${server.guild.id}")
        if (req.code() != 200) {
            // todo handle
            req.close()
            close()
            return@produce
        }
        val json = JSONObject(req.body().string()).getJSONArray("roles")
        req.close()
        for (item in json) {
            val role = ServerRole(item as JSONObject)
            cache[role.id] = role.bits
            send(role)
        }
        close()
    }

    suspend fun add(id: String, bits: Int) {
        val req = server.requester.executeJSON(Method.POST, "/permissions/${server.guild.id}", JSONObject()
                .put("id", id).put("bits", bits))
        if (req.code() != 200) {
            // todo handle
            req.close()
            return
        }
        req.close()
        cache[id] = bits
    }

    suspend fun delete() {
        val req = server.requester.execute(Method.DELETE, "/permissions/${server.guild.id}")
        if (req.code() != 200) {
            // todo handle
            req.close()
            return
        }
        req.close()
        invalidateCache()
    }

    suspend fun getRole(id: String): Int? {
        if (cache.containsKey(id)) {
            return cache[id]
        }
        val req = server.requester.execute(Method.GET, "/permissions/${server.guild.id}/$id")
        if (req.code() != 200) {
            // todo handle
            req.close()
            cache[id] = null
            return null
        }
        val bits = JSONObject(req.body().string()).getInt("bits")
        req.close()
        cache[id] = bits
        return bits
    }

    suspend fun updateRole(id: String, bits: Int) {
        val req = server.requester.executeJSON(Method.PUT, "/permissions/${server.guild.id}/$id", JSONObject()
                .put("bits", bits))
        if (req.code() != 200) {
            // todo handle
            req.close()
            return
        }
        req.close()
        cache[id] = bits
    }

    suspend fun deleteRole(id: String) {
        val req = server.requester.execute(Method.DELETE, "/permissions/${server.guild.id}/$id")
        if (req.code() != 200) {
            // todo handle
            req.close()
            return
        }
        req.close()
        cache.remove(id)
    }
}