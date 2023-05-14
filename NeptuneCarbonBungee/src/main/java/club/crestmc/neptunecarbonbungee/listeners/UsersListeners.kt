package club.crestmc.neptunecarbonbungee.listeners

import club.crestmc.neptunecarbonbungee.NeptuneCarbonBungee
import club.crestmc.neptunecarbonbungee.users.UserManager
import co.aikar.commands.BaseCommand
import com.mongodb.client.model.Filters.eq
import net.md_5.bungee.api.event.LoginEvent
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.event.PreLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import java.net.InetSocketAddress

class UsersListeners(val plugin: NeptuneCarbonBungee) : Listener {
    @EventHandler
    fun onJoin(e: PostLoginEvent) {
        plugin.logger.info("Checking player data for ${e.player.name}...")
        val check = plugin.databaseManager.usersCollection.find(eq("uuid", e.player.uniqueId.toString()))
        if(check.first() == null) {
            UserManager(plugin).createUser(e.player.uniqueId, e.player.name, (e.player.socketAddress as InetSocketAddress).hostName)
            plugin.logger.info("Created player data for ${e.player.name}, as they have never logged on before.")
        } else {
            UserManager(plugin).updateUser(e.player, check.first(), true)
        }
    }
}