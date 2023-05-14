package club.crestmc.neptunecarbonbungee.listeners

import club.crestmc.neptunecarbonbungee.NeptuneCarbonBungee
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.ProxyPingEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class PingListener(val plugin: NeptuneCarbonBungee) : Listener {

    private val motdMessages: List<String> = plugin.configManager.config?.getStringList("motd")!!

    @EventHandler
    fun onProxyPing(event: ProxyPingEvent) {
        val ping = event.response
        val motdBuilder = StringBuilder()
        val motd = StringBuilder()
        for (line in 0..1) {
            val motdMessage: String = motdMessages.get(line)
            motdBuilder.append(ChatColor.translateAlternateColorCodes('&', motdMessage))
            var lineLength = (59 - ChatColor.stripColor(motdMessages.get(0)).length) / 2
            if (line == 1) {
                lineLength = lineLength - 2
            }
            for (i in 0 until lineLength) {
                motdBuilder.insert(0, ' ')
                motdBuilder.append(' ')
            }
            motd.append(motdBuilder)
            if (line == 0) {
                motd.append("\n")
            }
            motdBuilder.delete(0, motdBuilder.length)
        }
        ping.descriptionComponent = TextComponent(motd.toString())
    }
}