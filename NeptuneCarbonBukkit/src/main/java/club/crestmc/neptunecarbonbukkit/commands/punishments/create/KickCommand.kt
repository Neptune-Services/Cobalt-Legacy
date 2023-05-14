package club.crestmc.neptunecarbonbukkit.commands.punishments.create

import club.crestmc.neptunecarbonbukkit.Constants
import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit
import club.crestmc.neptunecarbonbukkit.utils.ChatUtil
import club.crestmc.neptunecarbonbukkit.utils.ChatUtil.translate
import club.crestmc.neptunecarbonbukkit.utils.ColorUtil
import club.crestmc.neptunecarbonbukkit.utils.PluginMessageUtil.sendData
import club.crestmc.neptunecarbonbukkit.utils.UUIDUtil
import club.crestmc.neptunecarbonbukkit.utils.UUIDUtil.getUnknownPlayerDatabaseFromUsername
import club.crestmc.neptunecarbonbukkit.utils.UUIDUtil.getUnknownPlayerFromUsername
import club.crestmc.neptunecarbonbukkit.utils.Util
import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandIssuer
import co.aikar.commands.MessageKeys
import co.aikar.commands.MessageType
import co.aikar.commands.annotation.*
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.time.Instant
import java.util.*

@CommandAlias("kick|k")
@Description("Kick a player from the network.")
@CommandPermission("neptunecarbon.kick")
class KickCommand : BaseCommand() {
    @Dependency
    private val plugin: NeptuneCarbonBukkit? = null
    @CommandAlias("kick|k")
    @Description("Kick a player from the network.")
    @CommandPermission("neptunecarbon.kick")
    @Syntax("<target> [-s] <reason> [-s]")
    @CommandCompletion("@allOnline")
    fun kickCommandRun(sender: CommandIssuer, @Name("target") targetArg: String?, @Name("reason") reason: String) {
        var reason = reason
        var silent = false
        if (reason.contains("-s")) {
            silent = true
            reason = reason.replace("-s ".toRegex(), "")
            reason = reason.replace(" -s".toRegex(), "")
            reason = reason.replace("-s".toRegex(), "")
        }
        val target = getUnknownPlayerDatabaseFromUsername(targetArg!!)
        if (target == null) {
            plugin?.manager
                ?.sendMessage(sender, MessageType.ERROR, MessageKeys.COULD_NOT_FIND_PLAYER, "{search}", targetArg)
            return
        }
        var msg = translate(
            (if (silent) ChatUtil.getLanguageTranslation("kick.silent") else "") + ChatUtil.getLanguageTranslation("kick.youKicked")
                .replace("%target%", ColorUtil().getColor(target.uuid) + target.username!!)
                .replace("%reason%", reason)
        )
        msg = if (silent) {
            msg.replace("%silent%", ChatUtil.getLanguageTranslation("kick.silent"))
        } else {
            msg.replace("%silent%", "")
        }
        val senderName: String
        if (sender.isPlayer) {
            senderName = plugin!!.server.getPlayer(sender.uniqueId)!!.name
        } else {
            senderName = ChatUtil.getLanguageTranslation("consoleName")
        }
        val modUuid: String
        modUuid = if (sender.isPlayer) {
            sender.uniqueId.toString()
        } else {
            "console"
        }
        sendData(
            "BungeeCord", "KickPlayer", target.username, translate(
                ChatUtil.translate("&cYou were kicked from ${Constants.serverName}" +
                        "\n&cReason: &7${reason}")
            )
        )
        val toSave = Document()
            .append("uuid", target.uuid.toString())
        toSave.append("moderatorUuid", modUuid)
        toSave.append("type", "kick")
        toSave.append("date", Date.from(Instant.now()))
        toSave.append("punishmentId", "" + Util().pID)
        toSave.append("active", false)
        toSave.append("status", "active")
        toSave.append("reason", reason)
        toSave.append("server", plugin?.configManager?.config?.getString("serverName"))
        plugin?.databaseManager?.punishmentsCollection?.insertOne(toSave)
        sender.sendMessage(msg)
        val broadcastMsg = TextComponent(
            translate(
                (if (silent) ChatUtil.getLanguageTranslation("kick.silent") else "") + ChatUtil.getLanguageTranslation("kick.announcement")
                    .replace("%target%", ColorUtil().getColor(target.uuid) + target.username!!)
                    .replace("%player%", senderName)
                    .replace("%reason%", reason)
            )
        )
        for (p in Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("neptunecarbon.staff")) {
                val hoverEvent = HoverEvent(
                    HoverEvent.Action.SHOW_TEXT, ComponentBuilder(
                        translate(
                            ChatUtil.getLanguageTranslation("kick.hover")
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