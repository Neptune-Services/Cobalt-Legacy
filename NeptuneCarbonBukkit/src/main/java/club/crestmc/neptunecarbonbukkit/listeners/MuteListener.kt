package club.crestmc.neptunecarbonbukkit.listeners

import club.crestmc.neptunecarbonbukkit.Constants
import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit
import club.crestmc.neptunecarbonbukkit.PunishmentMessages
import club.crestmc.neptunecarbonbukkit.utils.ChatUtil
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerChatEvent

class MuteListener(val plugin: NeptuneCarbonBukkit) : Listener {
    @EventHandler
    fun onChatMuteHandler(e: AsyncPlayerChatEvent) {
        System.out.println("event fired")
        var doc = plugin.databaseManager.punishmentsCollection.find(and(
            eq("uuid", e.player.uniqueId.toString()),
            eq("type", "mute"),
            eq("active", true)
        ))

        if(doc.first() != null) {
            e.isCancelled = true

            if(doc.first().getDate("expires") == null) {
                e.player.sendMessage(ChatUtil.translate(
                    PunishmentMessages(plugin).getPermMuteMsg(doc.first().getString("reason"))
                ))
            }
        }
    }
}