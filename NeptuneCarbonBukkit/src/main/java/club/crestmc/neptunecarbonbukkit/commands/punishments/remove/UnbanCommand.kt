package club.crestmc.neptunecarbonbukkit.commands.punishments.remove

import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit
import club.crestmc.neptunecarbonbukkit.utils.*
import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandIssuer
import co.aikar.commands.MessageKeys
import co.aikar.commands.MessageType
import co.aikar.commands.annotation.*
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.time.Instant
import java.util.*

@CommandAlias("unban|ub")
@Description("Unban a player from the network.")
class UnbanCommand : BaseCommand() {
    @Dependency
    lateinit var plugin: NeptuneCarbonBukkit

    @CommandAlias("unban|ub")
    @Description("Unban a player from the network.")
    @CommandPermission("neptunecarbon.unban")
    @Syntax("<target> [-s] <reason> [-s]")
    @CommandCompletion("@allOnline")
    fun onUnbanRun(sender: CommandIssuer, @Name("target") targetArg: String?, @Name("reason") reason: String) {
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

        val existsCheck = plugin.databaseManager.punishmentsCollection.find(and(eq("uuid", target.uuid.toString()), eq("active", true), eq("type", "ban")))
        if (existsCheck.first() == null) {
            sender.sendMessage(ChatUtil.translate("&cThat player is not banned."))
            return
        }

        val addedReason = existsCheck.first().getString("reason")

        val msg = ChatUtil.translate(
            (if (silent) ChatUtil.translate("&7(Silent) ") else "") + "&aYou have unbanned ${ColorUtil().getColoredName(target.uuid)} &afor &f${reason}&a."
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

        //plugin.getDatabaseManager().punishments.insertOne(toSave);

        //Document toUpdate = new Document("$set", new Document("active", false)).append("$set", new Document("status", "revoked"));

        //plugin.getDatabaseManager().punishments.insertOne(toSave);

        //Document toUpdate = new Document("$set", new Document("active", false)).append("$set", new Document("status", "revoked"));
        val update = Updates.combine(
            Updates.set("active", false),
            Updates.set("status", "revoked"),
            Updates.set("revokedBy", modUuid),
            Updates.set("revokedDate", Date.from(Instant.now())),
            Updates.set("revokedReason", reason),
            Updates.set("revokedServer", plugin.configManager.config?.getString("serverName"))
        )
        plugin.databaseManager.punishmentsCollection.updateOne(existsCheck.first().toBsonDocument(), update)

        sender.sendMessage(msg)
        val broadcastMsg = TextComponent(
            ChatUtil.translate(
                (if (silent) ChatUtil.translate("&7(Silent) ") else "") + "$senderName &ahas unbanned ${ColorUtil().getColoredName(target.uuid)}&a."
            )
        )
        for (p in Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("neptunecarbon.staff")) {
                val hoverEvent = HoverEvent(
                    HoverEvent.Action.SHOW_TEXT, ComponentBuilder(
                        ChatUtil.translate(
                            ChatUtil.getLanguageTranslation("hover.removed")
                                .replace("%player%", senderName)
                                .replace("%removed_reason%", reason)
                                .replace("%added_reason%", addedReason)
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