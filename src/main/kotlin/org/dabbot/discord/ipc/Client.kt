package org.dabbot.discord.ipc

import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketFactory
import org.dabbot.discord.Shard
import org.json.JSONArray
import java.util.*
import javax.script.ScriptEngineManager
import javax.script.ScriptException

class Client(val shard: Shard) {
    private val engineManager = ScriptEngineManager()
    private val factory = WebSocketFactory()
    private val ws: WebSocket
    private val handlers = HashMap<MessageType, (data: Array<Any?>) -> Unit>()
    private var state: State? = null

    init {
        val apiConfig = shard.config.getTable("api")
        val token = apiConfig.getString("token")
        val endpoint = apiConfig.getString("wsEndpoint")
        ws = factory.createSocket(endpoint)
                .addHeader("Authorization", token)
                .addListener(object : WebSocketAdapter() {
                    override fun onTextMessage(ws: WebSocket?, text: String?) {
                        try {
                            val json = JSONArray(text)
                            val typeId = json.getInt(0)
                            MessageType.values().forEach { v ->
                                if (v.value == typeId) {
                                    val arr = arrayOfNulls<Any?>(json.length() - 1)
                                    for (i in 1..json.length() - 1) {
                                        arr[i - 1] = json.get(i)
                                    }
                                    handlers[v]!!(arr)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
        handlers[MessageType.WELCOME] = { data ->
            state = State(data[0] as Int)
        }
        handlers[MessageType.EVAL_REQ] = { data ->
            try {
                val engine = engineManager.getEngineByName("nashorn")
                engine.put("shard", shard)
                var result = engine.eval(data[0] as String)
                if (result == null) {
                    result = "null"
                } else {
                    result = result.toString()
                }
                println("EVAL_REQ result: $result")
                send(MessageType.EVAL_RESP, result)
            } catch (e: ScriptException) {
                // todo send as response
                e.printStackTrace()
            }
        }
    }

    private class State(val id: Int)

    fun connect(): Client {
        ws.connect()
        send(MessageType.HELLO, ClientType.BOT.value, shard.shard)
        return this
    }

    fun disconnect(): Client {
        send(MessageType.BYE)
        ws.disconnect()
        return this
    }

    private fun send(type: MessageType, vararg data: Any): Client {
        val a = JSONArray().put(type.value)
        data.forEach { item -> a.put(item) }
        ws.sendText(a.toString())
        return this
    }
}