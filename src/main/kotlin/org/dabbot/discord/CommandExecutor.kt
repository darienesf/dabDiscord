@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package org.dabbot.discord

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

class CommandExecutor {
    fun execute(command: Command, ctx: Command.Context) {
        launch(CommonPool) {
            command.on(ctx)
        }
    }
}