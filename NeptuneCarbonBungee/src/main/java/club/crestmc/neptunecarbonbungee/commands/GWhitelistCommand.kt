package club.crestmc.neptunecarbonbungee.commands

import club.crestmc.neptunecarbonbungee.NeptuneCarbonBungee
import club.crestmc.neptunecarbonbungee.utils.ChatUtil
import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.CommandIssuer
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Dependency
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.HelpCommand
import co.aikar.commands.annotation.Subcommand

@CommandAlias("gwhitelist")
@Description("Manage the global whitelist.")
@CommandPermission("carbon.gwhitelist")
class GWhitelistCommand : BaseCommand() {
    @Dependency
    lateinit var plugin: NeptuneCarbonBungee

    @Subcommand("add")
    @Description("Add a player to the global whitelist.")
    @CommandCompletion("@allOnline")
    fun onWhitelistAdd(issuer: CommandIssuer) {
        issuer.sendMessage(ChatUtil.translate("&cThis feature has not yet been implemented, sorry :("))
    }

    @Default
    @HelpCommand
    fun onHelp(issuer: CommandIssuer, help: CommandHelp) {
        help.showHelp()
    }
}