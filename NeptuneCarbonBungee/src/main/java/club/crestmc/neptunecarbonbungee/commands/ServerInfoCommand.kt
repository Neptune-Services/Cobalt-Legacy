package club.crestmc.neptunecarbonbungee.commands

import club.crestmc.neptunecarbonbungee.Constants
import club.crestmc.neptunecarbonbungee.NeptuneCarbonBungee
import club.crestmc.neptunecarbonbungee.utils.ChatUtil
import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandIssuer
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Dependency
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Name
import com.mongodb.client.model.Filters.eq

class ServerInfoCommand : BaseCommand() {

    @Dependency
    lateinit var plugin: NeptuneCarbonBungee

    @CommandAlias("serverinfo")
    @CommandPermission("cobalt.serverinfo")
    @Description("View the information about a specific server.")
    fun onServerInfo(issuer: CommandIssuer, @Name("server") serverArg: String) {
        val docSearch = plugin.databaseManager.serverStatusCollection.find(eq("serverName", serverArg.lowercase()))

        if(docSearch.first() != null) {
            if(docSearch.first().getLong("lastChecked") < System.currentTimeMillis() / 1000 - 10) {
                issuer.sendMessage(ChatUtil.translate("&cThat server last updated it's status over 10 seconds ago, but has not told the proxy it is offline. This may mean it has crashed. Please contact a developer."))
                return
            }
        }

        if(docSearch.first() == null) {
            issuer.sendMessage(ChatUtil.translate("&cNo server matching &e$serverArg&c is connected to the database. Is it online?"))
            return
        }

        issuer.sendMessage(ChatUtil.translate("&7&m---------------------------------------\n" +
                "&a&lViewing status for ${serverArg.lowercase()}\n" +
                "&7\n" +
                "&d Status: ${docSearch.first().getBoolean("whitelisted").let {
                    if (it) "&eWhitelisted" else "&aOnline"
                }}\n" +
                "&d Players: &f${docSearch.first().getInteger("playercountTotal")}/${docSearch.first().getInteger("maxplayers")}\n" +
                "&7&m---------------------------------------"))
    }
}