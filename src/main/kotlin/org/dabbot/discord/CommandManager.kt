package org.dabbot.discord

import com.moandjiezana.toml.Toml
import org.dabbot.discord.command.*
import java.util.*

class CommandManager(config: Toml) {
    val commands: MutableMap<String, Command> = HashMap()

    init {
        register(
                AboutCommand(),
                AdminCommand(config),
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