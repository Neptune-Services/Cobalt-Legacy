package club.crestmc.neptunecarbonbukkit.commands.punishments.info

import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit
import club.crestmc.neptunecarbonbukkit.entities.UnknownPlayer
import club.crestmc.neptunecarbonbukkit.gui.GUI
import club.crestmc.neptunecarbonbukkit.punishmenthistory.guis.CasesMain
import club.crestmc.neptunecarbonbukkit.utils.ChatUtil
import club.crestmc.neptunecarbonbukkit.utils.ColorUtil
import club.crestmc.neptunecarbonbukkit.utils.UUIDUtil
import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandIssuer
import co.aikar.commands.MessageKeys
import co.aikar.commands.MessageType
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Dependency
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Name
import com.mongodb.client.model.Filters.eq
import org.bukkit.Bukkit

@CommandAlias("hist|history|cases|punishments|modlogs")
@Description("View punishment logs for a player.")
@CommandPermission("carbon.hist")
class HistCommand : BaseCommand() {

    @Dependency
    lateinit var plugin: NeptuneCarbonBukkit

    @Default
    @CommandCompletion("@allOnline")
    fun histCommand(issuer: CommandIssuer, @Name("target") targetArg: String) {
        if(!issuer.isPlayer) {
            issuer.sendMessage(ChatUtil.translate("&cI'm sorry, but this command can only be run by players."))
        }
        val player = Bukkit.getServer().getPlayer(issuer.uniqueId)!!
        val target = UUIDUtil.getUnknownPlayerDatabaseFromUsername(targetArg!!)
        if (target == null) {
            plugin.manager
                .sendMessage(issuer, MessageType.ERROR, MessageKeys.COULD_NOT_FIND_PLAYER, "{search}", targetArg)
            return
        }

        player.sendMessage(ChatUtil.translate("&aGathering history for ${target.username}, this may take some time..."))

        if(plugin.databaseManager.punishmentsCollection.find(eq("uuid", target.uuid.toString())).first() == null) {
            player.sendMessage(ChatUtil.translate(
                ColorUtil().getColoredName(target.uuid)
                + " &chas no punishment history."
            ))
            return
        }

        val casesMain = CasesMain(player, 9, ChatUtil.translate("&2Select Type")).setup(player, target)
        GUI.open(CasesMain.getGui())
    }
}