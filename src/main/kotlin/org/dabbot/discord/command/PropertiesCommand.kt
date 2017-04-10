package org.dabbot.discord.command

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.dabbot.discord.Command
import org.dabbot.discord.Permission
import org.json.JSONObject

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class PropertiesCommand: Command(Permission.PROPERTIES, "properties", "propertie", "settings", "setting", "propertys", "options") {
    override fun on(ctx: Context) {
        launch(CommonPool) {
            val properties = ctx.server.propertyManager.list()
            if (properties == null || properties.length() == 0) {
                ctx.reply("Server has no properties!")
                return@launch
            }
            val builder = StringBuilder("**Server properties:**")
            for (property in properties) {
                if (property is JSONObject) {
                    builder.append("\n${property.getString("id")}: `${property.getString("value")}`")
                }
            }
            ctx.reply(builder.toString())
        }
    }
}