package org.dabbot.discord.ipc

enum class MessageType(val value: Int) {
    HELLO(0),
    WELCOME(1),
    BYE(2),
    EVAL_REQ(3),
    EVAL_RESP(4)
}

enum class ClientType(val value: Int) {
    BOT(0),
    WEB(1)
}

enum class EvalType(val value: Int) {
    SHARD(0),
    RANGE(1),
    ALL(2)
}