package club.crestmc.neptunecarbonbukkit.commands.punishments.create

import club.crestmc.neptunecarbonbukkit.Constants
import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit
import club.crestmc.neptunecarbonbukkit.utils.*
import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandIssuer
import co.aikar.commands.MessageKeys
import co.aikar.commands.MessageType
import co.aikar.commands.annotation.*
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.time.Instant
import java.util.*

@CommandAlias("mute|m")
@Description("Permanently mute a player from the network.")
class MuteCommand : BaseCommand() {
    @Dependency
    lateinit var plugin: NeptuneCarbonBukkit

    @CommandAlias("mute|m")
    @Description("Permanently mute a player from the network.")
    @CommandPermission("neptunecarbon.mute")
    @Syntax("<target> [-s] <reason> [-s]")
    @CommandCompletion("@allOnline")
    fun onBanRun(sender: CommandIssuer, @Name("target") targetArg: String?, @Name("reason") reason: String) {
        var reason = reason
        var silent = false
        if (reason.contains("-s")) {
            silent = true
            reason = reason.replace("-s ".toRegex(), "")
            reason = reason.replace(" -s".toRegex(), "")
            reason = reason.replace("-s".toRegex(), "")
        }
        val target = UUIDUtil.getUnknownPlayerDatabaseFromUsername(targetArg!!)
        if (target == null) {
            plugin.manager
                .sendMessage(sender, MessageType.ERROR, MessageKeys.COULD_NOT_FIND_PLAYER, "{search}", targetArg)
            return
        }

        val targetPlayer = plugin.server.getPlayer(target.uuid!!)

        val existsCheck = plugin.databaseManager.punishmentsCollection.find(and(eq("uuid", target.uuid.toString()), eq("active", true), eq("type", "mute")))
        if(existsCheck.first() != null) {
            sender.sendMessage(ChatUtil.translate("&cThat player is already muted."))
            return
        }

        val msg = ChatUtil.translate(
            (if (silent) ChatUtil.translate("&7(Silent) ") else "") + "&aYou have permanently muted ${ColorUtil().getColor(target.uuid) + target.username!!} &afor &f${reason}&a."
        )

        val senderName: String
        if (sender.isPlayer) {
            senderName = ColorUtil().getColor(sender.uniqueId) + plugin.server.getPlayer(sender.uniqueId)!!.name
        } else {
            senderName = ChatUtil.getLanguageTranslation("consoleName")
        }
        val modUuid: String
        modUuid = if (sender.isPlayer) {
            sender.uniqueId.toString()
        } else {
            "console"
        }

        if(targetPlayer != null && targetPlayer.isOnline) {
            targetPlayer.sendMessage(ChatUtil.translate(Constants.getPermMuteMsg(reason)))
        }

        val toSave = Document()
            .append("uuid", target.uuid.toString())
        toSave.append("moderatorUuid", modUuid)
        toSave.append("type", "mute")
        toSave.append("date", Date.from(Instant.now()))
        toSave.append("punishmentId", "" + Util().pID)
        toSave.append("active", true)
        toSave.append("status", "active")
        toSave.append("reason", reason)
        toSave.append("server", plugin.configManager.config?.getString("serverName"))
        plugin.databaseManager.punishmentsCollection.insertOne(toSave)
        sender.sendMessage(msg)
        val broadcastMsg = TextComponent(
            ChatUtil.translate(
                (if (silent) ChatUtil.translate("&7(Silent) ") else "") + "$senderName &ahas permanently muted ${ColorUtil().getColor(target.uuid) + target.username!!}&a."
            )
        )
        for (p in Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("neptunecarbon.staff")) {
                val hoverEvent = HoverEvent(
                    HoverEvent.Action.SHOW_TEXT, ComponentBuilder(
                        ChatUtil.translate(
                            ChatUtil.getLanguageTranslation("hover.added")
                                .replace("%player%", senderName)
                                .replace("%reason%", reason)
                        )
                    ).create()
                )
                broadcastMsg.hoverEvent = hoverEvent
            }
            if (silent) {
                if (p.hasPermission("neptunecarbon.staff")) {
                    p.sendMessage(broadcastMsg)
                }
            } else {
                p.sendMessage(broadcastMsg)
            }
        }
    }
}