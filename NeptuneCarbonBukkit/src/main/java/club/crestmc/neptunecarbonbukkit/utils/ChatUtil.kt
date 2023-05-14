package club.crestmc.neptunecarbonbukkit.utils

import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin

object ChatUtil {
    private var plugin: NeptuneCarbonBukkit? = null

    init {
        plugin = JavaPlugin.getPlugin(
            NeptuneCarbonBukkit::class.java
        )
    }

    fun translate(text: String?): String {
        return ChatColor.translateAlternateColorCodes('&', text!!)
    }

    fun getLanguageTranslation(entry: String): String {
        return translate(plugin?.configManager?.languageConfig?.getString(entry))
    }
}