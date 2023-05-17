package club.crestmc.neptunecarbonbukkit.commands.punishments.create

import club.crestmc.neptunecarbonbukkit.Constants
import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit
import club.crestmc.neptunecarbonbukkit.PunishmentMessages
import club.crestmc.neptunecarbonbukkit.entities.UnknownPlayer
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

@CommandAlias("ban|b")
@Description("Ban a player from the network.")
class BlacklistCommand : BaseCommand() {
    @Dependency
    lateinit var plugin: NeptuneCarbonBukkit

    @CommandAlias("blacklist|bl")
    @Description("Blacklist a player from the network.")
    @CommandPermission("neptunecarbon.blacklist")
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

        val existsCheck = plugin.databaseManager.punishmentsCollection.find(and(eq("uuid", target.uuid.toString()), eq("active", true), eq("type", "blacklist")))
        if(existsCheck.first() != null) {
            sender.sendMessage(ChatUtil.translate("&cThat user is already blacklisted."))
            return
        }

        val msg = ChatUtil.translate(
            (if (silent) ChatUtil.translate("&7(Silent) ") else "") + "&aYou have permanently blacklisted ${ColorUtil().getColor(target.uuid) + target.username!!} &afor &f${reason}&a."
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
        PluginMessageUtil.sendData(
            "BungeeCord", "KickPlayer", targetArg, ChatUtil.translate(
                PunishmentMessages(plugin).getPermBlacklistMsg(reason, null)
            )
        )
        for(alt: UnknownPlayer in PlayerUtils(plugin).getAlts(target)) {
            PluginMessageUtil.sendData(
                "BungeeCord", "KickPlayer", alt.username, ChatUtil.translate(
                    PunishmentMessages(plugin).getPermBlacklistMsg(reason, target.username)
                )
            )
        }
        val toSave = Document()
            .append("uuid", target.uuid.toString())
        toSave.append("moderatorUuid", modUuid)
        toSave.append("type", "blacklist")
        toSave.append("date", Date.from(Instant.now()))
        toSave.append("punishmentId", "" + Util().pID)
        toSave.append("active", true)
        toSave.append("status", "active")
        toSave.append("reason", reason)
        toSave.append("ips", plugin.databaseManager.usersCollection.find(eq("uuid", target.uuid.toString())).first().get("ips") as MutableList<String>)
        toSave.append("server", plugin.configManager.config?.getString("serverName"))
        plugin.databaseManager.punishmentsCollection.insertOne(toSave)
        sender.sendMessage(msg)
        val broadcastMsg = TextComponent(
            ChatUtil.translate(
                (if (silent) ChatUtil.translate("&7(Silent) ") else "") + "$senderName &ahas permanently blacklisted ${ColorUtil().getColor(target.uuid) + target.username!!}&a."
            )
        )
        for (p in Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("neptunecarbon.staff")) {
                val hoverEvent = HoverEvent(
                    HoverEvent.Action.SHOW_TEXT, ComponentBuilder(
                        ChatUtil.translate(
                            ChatUtil.getLanguageTranslation("ban.permanent.hover")
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