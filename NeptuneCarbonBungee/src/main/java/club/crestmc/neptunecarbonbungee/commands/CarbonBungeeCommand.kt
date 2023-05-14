package club.crestmc.neptunecarbonbungee.commands

import club.crestmc.neptunecarbonbungee.Constants
import club.crestmc.neptunecarbonbungee.NeptuneCarbonBungee
import club.crestmc.neptunecarbonbungee.utils.ChatUtil
import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.CommandIssuer
import co.aikar.commands.annotation.*

@CommandAlias("carbonbungee|cbungee")
@CommandPermission("carbon.about")
@Description("View information or reload the plugin.")
class CarbonBungeeCommand: BaseCommand() {
    @Dependency
    lateinit var plugin: NeptuneCarbonBungee

    @Default
    @HelpCommand
    fun onHelp(sender: CommandIssuer, help: CommandHelp) {
        help.showHelp()
    }

    @Subcommand("reload|rl")
    @Description("Reload all configuration files.")
    @CommandPermission("carbon.reload")
    fun onReload(sender: CommandIssuer) {
        sender.sendMessage(ChatUtil.translate("&aReloading &eCarbon Bungee&a..."))
        plugin.configManager.reloadConfig()
        plugin.configManager.reloadLanguage()
        sender.sendMessage(ChatUtil.translate("&aSuccessfully reloaded &eCarbon Bungee&a."))
        sendAboutMessage(sender)
    }

    @Subcommand("about|info")
    @Description("View plugin information.")
    @CommandPermission("carbon.about")
    fun onAbout(sender: CommandIssuer) {
        sendAboutMessage(sender)
    }

    private fun sendAboutMessage(sender: CommandIssuer) {
        sender.sendMessage(ChatUtil.translate("&eThis server is running &dNeptune Carbon Bungee&e licensed to &d" + Constants.serverName + "&e." +
                "\n&eIf this plugin is being used on a server that is not &d" + Constants.serverName + "&e, contact&d " + Constants.instanceSupervisor + " &eimmediately."))
    }

}