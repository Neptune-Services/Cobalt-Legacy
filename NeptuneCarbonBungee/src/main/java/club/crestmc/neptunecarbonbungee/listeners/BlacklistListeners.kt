package club.crestmc.neptunecarbonbungee.listeners

import club.crestmc.neptunecarbonbungee.Constants
import club.crestmc.neptunecarbonbungee.NeptuneCarbonBungee
import club.crestmc.neptunecarbonbungee.PunishmentMessages
import club.crestmc.neptunecarbonbungee.utils.ChatUtil
import com.mongodb.BasicDBObject
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.api.event.LoginEvent
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.event.PreLoginEvent
import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

class BlacklistListeners(val plugin: NeptuneCarbonBungee) : Listener {
    @EventHandler
    fun onJoin(e: PostLoginEvent) {
        val user = plugin.databaseManager.usersCollection.find(eq("uuid", e.player.uniqueId.toString())).first()

        val ipCheck = plugin.databaseManager.punishmentsCollection.find(and(
            eq("type", "blacklist"),
            eq("uuid", e.player.uniqueId.toString()),
            eq("active", true)
        ))

        if(ipCheck.first() != null) {
            if(!((ipCheck.first().get("ips") as MutableList<String>).contains((e.player.socketAddress as InetSocketAddress).hostName))) {
                plugin.logger.info("Not the same, just isnt the same")
                val newArray = (ipCheck.first().get("ips") as MutableList<String>)
                newArray.add((e.player.socketAddress as InetSocketAddress).hostName)
                plugin.databaseManager.punishmentsCollection.updateOne(ipCheck.first(), Updates.combine(
                    Updates.set("ips", newArray)
                ))

                e.player.disconnect(Constants.getPermBlacklistMsg(ipCheck.first().getString("reason"), null))
                return
            }
        }

        val ipsArray = (user.get("ips") as MutableList<String>)
        if(!(ipsArray.contains((e.player.socketAddress as InetSocketAddress).hostName))) {
            ipsArray.add((e.player.socketAddress as InetSocketAddress).hostName)
        }

        val docCheck = plugin.databaseManager.punishmentsCollection.find(and(
            eq("type", "blacklist"),
            eq("ips", BasicDBObject("\$in", ipsArray)),
            eq("active", true)
        ))

        if(docCheck.first() != null) {
            if(docCheck.first().getDate("expires") == null) {
                plugin.proxy.scheduler.schedule(plugin, {
                    e.player.sendMessage(
                        PunishmentMessages(plugin).getPermBlacklistMsg(
                        
                            docCheck.first().getString("reason")!!, 
                        
                            if(docCheck.first().getString("uuid") != e.player.uniqueId.toString()) plugin.databaseManager.usersCollection.find(eq("uuid", docCheck.first().getString("uuid"))).first().getString("username")
                            else null
                        
                        ))
                }, 1, TimeUnit.SECONDS)
                plugin.blacklistedPlayers.remove(e.player.uniqueId)
                plugin.blacklistedPlayers.add(e.player.uniqueId)
            }
        }
    }

    @EventHandler
    fun onLeave(e: PlayerDisconnectEvent) {
        plugin.blacklistedPlayers.remove(e.player.uniqueId)
    }

    @EventHandler
    fun onChat(e: ChatEvent) {
        val player = (e.sender as ProxiedPlayer)
        if(plugin.blacklistedPlayers.contains((player.uniqueId))) {
            if(e.isCommand) {
                if(!e.message.startsWith("/link", true)) {
                    e.isCancelled = true
                    player.sendMessage(ChatUtil.translate("&cYou are not allowed to use commands as you are blacklisted. The only command which you may run is /link."))
                }
            } else {
                e.isCancelled = true
                player.sendMessage(ChatUtil.translate("&cYou are not allowed to send chat messages as you are blacklisted."))
            }
        }
    }

    @EventHandler
    fun onServerSwitchyswitch(e: ServerConnectEvent) {
        plugin.logger.info("parent")
        if (e.reason != ServerConnectEvent.Reason.JOIN_PROXY) {
            plugin.logger.info("server switch")
            if(plugin.blacklistedPlayers.contains(e.player.uniqueId)) {
                plugin.logger.info("cancelling switch")
                e.isCancelled = true
                e.player.sendMessage(ChatUtil.translate(
                    "&cYour connection to the server &c&l${e.target.name}&c has been prevented due to you being blacklisted."
                ))
            }
        }

        if(e.reason == ServerConnectEvent.Reason.JOIN_PROXY) {
            plugin.logger.info("server ocnnect")
            if(plugin.blacklistedPlayers.contains(e.player.uniqueId)) {
                plugin.logger.info("cancelling?")
                if(!plugin.configManager.config!!.getStringList("allowedServersBanned").contains(e.target.name)) {
                    plugin.logger.info("CANCELLING!")
                    e.player.disconnect(ChatUtil.translate("&cThere are no hubs available to send you to."))
                }
            }
        }
    }
}