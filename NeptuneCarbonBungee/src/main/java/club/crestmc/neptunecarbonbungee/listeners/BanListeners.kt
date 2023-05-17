package club.crestmc.neptunecarbonbungee.listeners

import club.crestmc.neptunecarbonbungee.Constants
import club.crestmc.neptunecarbonbungee.NeptuneCarbonBungee
import club.crestmc.neptunecarbonbungee.PunishmentMessages
import club.crestmc.neptunecarbonbungee.utils.ChatUtil
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.api.event.LoginEvent
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.event.PreLoginEvent
import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import java.util.concurrent.TimeUnit

class BanListeners(val plugin: NeptuneCarbonBungee) : Listener {
    @EventHandler
    fun onJoin(e: PostLoginEvent) {
        val docCheck = plugin.databaseManager.punishmentsCollection.find(and(eq("uuid", e.player.uniqueId.toString()), eq("active", true), eq("type", "ban")))

        if(docCheck.first() != null) {
            if(docCheck.first().getDate("expires") == null) {
                plugin.proxy.scheduler.schedule(plugin, {
                    if(plugin.blacklistedPlayers.contains(e.player.uniqueId)) {
                        plugin.bannedPlayers.remove(e.player.uniqueId)
                    } else {
                        e.player.sendMessage(PunishmentMessages(plugin).getPermBanMsg(docCheck.first().getString("reason")!!))
                    }
                }, 1300, TimeUnit.MILLISECONDS)
                plugin.bannedPlayers.remove(e.player.uniqueId)
                plugin.bannedPlayers.add(e.player.uniqueId)
            }
        }
    }

    @EventHandler
    fun onLeave(e: PlayerDisconnectEvent) {
        plugin.bannedPlayers.remove(e.player.uniqueId)
    }

    @EventHandler
    fun onChat(e: ChatEvent) {
        val player = (e.sender as ProxiedPlayer)
        if(plugin.bannedPlayers.contains((player.uniqueId))) {
            if(e.isCommand) {
                if(!e.message.startsWith("/link", true)) {
                    e.isCancelled = true
                    player.sendMessage(ChatUtil.translate("&cYou are not allowed to use commands as you are banned. The only command which you may run is /link."))
                }
            } else {
                e.isCancelled = true
                player.sendMessage(ChatUtil.translate("&cYou are not allowed to send chat messages as you are banned."))
            }
        }
    }

    @EventHandler
    fun onServerSwitchyswitch(e: ServerConnectEvent) {
        plugin.logger.info("parent")
        if (e.reason != ServerConnectEvent.Reason.JOIN_PROXY) {
            plugin.logger.info("server switch")
            if(plugin.bannedPlayers.contains(e.player.uniqueId)) {
                plugin.logger.info("cancelling switch")
                e.isCancelled = true
                e.player.sendMessage(ChatUtil.translate("&cYour connection to the server &c&l${e.target.name}&c has been prevented due to you being banned."))
            }
        }

        if(e.reason == ServerConnectEvent.Reason.JOIN_PROXY) {
            plugin.logger.info("server ocnnect")
            if(plugin.bannedPlayers.contains(e.player.uniqueId)) {
                plugin.logger.info("cancelling?")
                if(!plugin.configManager.config!!.getStringList("allowedServersBanned").contains(e.target.name)) {
                    plugin.logger.info("CANCELLING!")
                    e.player.disconnect(ChatUtil.translate("&cThere are no hubs available to send you to."))
                }
            }
        }
    }
}