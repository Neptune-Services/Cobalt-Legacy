package club.crestmc.neptunecarbonbukkit.listeners

import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable

class UpdateServerListeners(val plugin: NeptuneCarbonBukkit) : Listener {
    @EventHandler
    fun onJoin(e: PlayerLoginEvent) {
        plugin.logger.info("JOine duverent ven Fitern")
        plugin.server.scheduler.runTaskLaterAsynchronously(plugin, Runnable {
            plugin.updateOnce()
        }, 5)
    }

    @EventHandler
    fun onLeave(e: PlayerQuitEvent) {
        plugin.logger.info("a player left or snthg")
        plugin.server.scheduler.runTaskLaterAsynchronously(plugin, Runnable {
            plugin.updateOnce()
        }, 5)
    }
}