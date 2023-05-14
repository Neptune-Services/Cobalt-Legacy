package club.crestmc.neptunecarbonbukkit.commands

import club.crestmc.neptunecarbonbukkit.Constants
import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit
import club.crestmc.neptunecarbonbukkit.utils.ChatUtil
import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.CommandIssuer
import co.aikar.commands.annotation.*

@CommandAlias("carbonbukkit|cbukkit")
@CommandPermission("carbon.about")
@Description("View information or reload the plugin.")
class CarbonBukkitCommand: BaseCommand() {
    @Dependency
    lateinit var plugin: NeptuneCarbonBukkit

    @Default
    @HelpCommand
    fun onHelp(sender: CommandIssuer, help: CommandHelp) {
        help.showHelp()
    }

    @Subcommand("reload|rl")
    @Description("Reload all configuration files.")
    @CommandPermission("carbon.reload")
    fun onReload(sender: CommandIssuer) {
        sender.sendMessage(ChatUtil.translate("&aReloading &eCarbon Bukkit&a..."))
        plugin.configManager.reloadConfig()
        plugin.configManager.reloadLanguageConfig()
        sender.sendMessage(ChatUtil.translate("&aSuccessfully reloaded &eCarbon Bukkit&a."))
        sendAboutMessage(sender)
    }

    @Subcommand("about|info")
    @Description("View plugin information.")
    @CommandPermission("carbon.about")
    fun onAbout(sender: CommandIssuer) {
        sendAboutMessage(sender)
    }

    private fun sendAboutMessage(sender: CommandIssuer) {
        sender.sendMessage(ChatUtil.translate("&eThis server is running &dNeptune Carbon Bukkit&e licensed to &d" + Constants.serverName + "&e." +
                "\n&eIf this plugin is being used on a server that is not &d" + Constants.serverName + "&e, contact&d " + Constants.instanceSupervisor + " &eimmediately."))
    }

}