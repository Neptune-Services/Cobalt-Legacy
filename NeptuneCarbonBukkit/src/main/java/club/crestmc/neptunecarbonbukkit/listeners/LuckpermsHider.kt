package club.crestmc.neptunecarbonbukkit.listeners

import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChatTabCompleteEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.server.TabCompleteEvent

class LuckpermsHider(val plugin: NeptuneCarbonBukkit) : Listener {
    @EventHandler
    fun onCommandRun(e: PlayerCommandPreprocessEvent) {
        if(e.message.startsWith("/luckperms:", ignoreCase = true)
            || e.message.startsWith("/luckperms", ignoreCase = true)
            || e.message.startsWith("/perm", ignoreCase = true)
            || e.message.startsWith("/permission", ignoreCase = true)
            || e.message.startsWith("/perms", ignoreCase = true)
            || e.message.startsWith("/permissions", ignoreCase = true)
            || e.message.startsWith("/lp ", ignoreCase = true) || e.message.equals("/lp", ignoreCase = true)) {
            plugin.logger.info("debug 1")
            if(!e.player.hasPermission("luckperms.bypassHider")) {
                plugin.logger.info("debug 2")
                e.isCancelled = true
                e.player.sendMessage("Unknown command. Type \"/help\" for help.")
            }
        }
    }

    @EventHandler
    fun onTabComplete(e: PlayerChatTabCompleteEvent) {
        plugin.logger.info("debug 0 tab")
        if(!e.player.hasPermission("luckperms.bypassHider")) {
            plugin.logger.info("debug 1 tab")
            e.tabCompletions.removeIf { !it.startsWith("/luckperms:", ignoreCase = true) }
            e.tabCompletions.removeIf { !it.startsWith("/luckperms", ignoreCase = true) }
            e.tabCompletions.removeIf { !it.startsWith("/lp ", ignoreCase = true) }
            e.tabCompletions.removeIf { !it.equals("/lp", ignoreCase = true) }
            e.tabCompletions.removeIf { !it.startsWith("/perm", ignoreCase = true) }
            e.tabCompletions.removeIf { !it.startsWith("/perms", ignoreCase = true) }
            e.tabCompletions.removeIf { !it.startsWith("/permissions", ignoreCase = true) }
            e.tabCompletions.removeIf { !it.startsWith("/permission", ignoreCase = true) }
        }
    }
}