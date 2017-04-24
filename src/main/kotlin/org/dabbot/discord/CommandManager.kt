package org.dabbot.discord

import org.reflections.Reflections
import java.util.*

class CommandManager {
    val commands: MutableMap<String, Command> = HashMap()

    init {
        /*register(
                AboutCommand(),
                AdminCommand(),
                AnnouncementsCommand(),
                ChooseCommand(),
                ClearCommand(),
                DebugCommand(),
                HelpCommand(),
                InviteCommand(),
                NowPlayingCommand(),
                PauseCommand(),
                PermissionsCommand(),
                PlayCommand(),
                PropertiesCommand(),
                ProvidersCommand(),
                QueueCommand(),
                RadioCommand(),
                RemoveCommand(),
                ReorderCommand(),
                RepeatCommand(),
                RestartCommand(),
                ResumeCommand(),
                SeekCommand(),
                ShuffleCommand(),
                SkipCommand(),
                StopCommand()
        )*/
        Reflections("org.dabbot.discord.command").getSubTypesOf(Command::class.java).forEach { subType ->
            val command = subType.newInstance()
            try {
                register(command)
            } catch (e: RuntimeException) {
                e.printStackTrace()
                return@forEach
            }
        }
    }

    fun register(vararg cmds: Command) {
        for (c in cmds) {
            for (n in c.names) {
                if (commands[n!!] != null) {
                    val className = c.javaClass.name
                    throw RuntimeException("Command name collision $n in $className!")
                }
                c.manager = this
                commands[n] = c
            }
        }
    }

    fun get(name: String): Command? {
        return commands[name]
    }
}