package club.crestmc.neptunecarbonbungee.utils

import club.crestmc.neptunecarbonbungee.NeptuneCarbonBungee
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ComponentBuilder

object ChatUtil {
    private val plugin: NeptuneCarbonBungee? = null

    fun translate(text: String?): String {
        return ChatColor.translateAlternateColorCodes('&', text)
    }
}