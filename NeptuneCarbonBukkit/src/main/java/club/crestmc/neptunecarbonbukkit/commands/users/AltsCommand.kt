package club.crestmc.neptunecarbonbukkit.commands.users

import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit
import club.crestmc.neptunecarbonbukkit.utils.ChatUtil
import club.crestmc.neptunecarbonbukkit.utils.PlayerUtils
import club.crestmc.neptunecarbonbukkit.utils.UUIDUtil
import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandIssuer
import co.aikar.commands.MessageKeys
import co.aikar.commands.MessageType
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Dependency
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Name

class AltsCommand : BaseCommand() {
    @Dependency
    lateinit var plugin: NeptuneCarbonBukkit

    @CommandAlias("alts")
    @Description("View the player's alternative accounts.")
    @CommandPermission("cobalt.alts")
    @CommandCompletion("@allOnline")
    fun onAltsRun(issuer: CommandIssuer, @Name("target") targetArg: String) {
        val target = UUIDUtil.getUnknownPlayerDatabaseFromUsername(targetArg!!)
        if (target == null) {
            plugin.manager
                .sendMessage(issuer, MessageType.ERROR, MessageKeys.COULD_NOT_FIND_PLAYER, "{search}", targetArg)
            return
        }

        val array = PlayerUtils(plugin).formatAlts(PlayerUtils(plugin).getAlts(target))
        if(array.isEmpty()) {
            issuer.sendMessage(ChatUtil.translate("&c${target.username} has no known alts."))
            return
        }

        issuer.sendMessage(ChatUtil.translate(
            "&aOnline&7, Offline, &6Muted&7, &cBanned&7, &4Blacklisted" +
                    "\n&aShowing alts for &e${target.username}&a:" +
                    "\n&7${array.toString().replace("[", "").replace("]", "")}"
        ))
    }
}