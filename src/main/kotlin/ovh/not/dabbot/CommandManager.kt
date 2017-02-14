package ovh.not.dabbot

import com.moandjiezana.toml.Toml
import ovh.not.dabbot.command.AdminCommand
import ovh.not.dabbot.command.PlayCommand
import java.util.*

class CommandManager(config: Toml, shard: ShardManager.Shard) {
    val commands: MutableMap<String, Command> = HashMap()

    init {
        register(
                AdminCommand(config),
                PlayCommand()
        )
    }

    fun register(vararg cmds: Command) {
        for (c in cmds) {
            for (n in c.names) {
                if (commands[n!!] != null) {
                    val className = c.javaClass.name
                    throw RuntimeException("Command name collision $n in $className!")
                }
                commands[n] = c
            }
        }
    }

    fun get(name: String): Command? {
        return commands[name]
    }
}