package club.crestmc.neptunecarbonbukkit.listeners

import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit
import club.crestmc.neptunecarbonbukkit.PunishmentMessages
import club.crestmc.neptunecarbonbukkit.utils.ChatUtil
import club.crestmc.neptunecarbonbukkit.utils.PlayerUtils
import club.crestmc.neptunecarbonbukkit.utils.PluginMessageUtil
import club.crestmc.neptunecarbonbukkit.utils.UUIDUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerLoginEvent

class BanEvasionListener(val plugin: NeptuneCarbonBukkit) : Listener {
    @EventHandler
    fun onJoinEvasion(e: PlayerLoginEvent) {
        val up = UUIDUtil.getUnknownPlayerDatabaseFromUuid(e.player.uniqueId.toString())!!
        val alts = PlayerUtils(plugin).getAlts(up)
        val banEvasion = PlayerUtils(plugin).getBanEvasion(alts, up)

        plugin.logger.info("yeah ban evasion checking")
        if(banEvasion != null) {
            if(!banEvasion!!.trim().equals(e.player.name, ignoreCase = true)) {
                plugin.logger.info("HOLY SHIT BAN EVADING OMG OMG BAN EVADING OF $banEvasion")
                plugin.server.scheduler.scheduleSyncDelayedTask(plugin, Runnable {
                    plugin.server.dispatchCommand(plugin.server.consoleSender, "blacklist $banEvasion -s Ban Evasion (${e.player.name})")
                    e.player.kickPlayer(ChatUtil.translate("&cYour user profile has been updated."))
                }, 10)
            }
        }
    }
}