package club.crestmc.neptunecarbonbukkit.listeners

import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit
import club.crestmc.neptunecarbonbukkit.utils.ChatUtil
import club.crestmc.neptunecarbonbukkit.utils.ColorUtil
import io.papermc.paper.event.player.ChatEvent
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ChatFormatter(val plugin: NeptuneCarbonBukkit) : Listener {
    @EventHandler
    fun chatFormatterMsgEvent(e: AsyncPlayerChatEvent) {
        e.format = PlaceholderAPI.setPlaceholders(e.player, ChatUtil.translate(
            plugin.configManager.config?.getString("chat.format")
                ?.replace("%prefix%", plugin.luckPermsAPI.userManager.getUser(e.player.uniqueId)!!.cachedData.metaData.prefix!!)
                ?.replace("%player%", ColorUtil().getColoredName(e.player.uniqueId)!!)
                ?.replace("%msg%", e.message.trim())
        ))
    }
}