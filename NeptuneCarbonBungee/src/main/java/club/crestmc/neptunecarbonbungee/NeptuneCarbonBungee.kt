package club.crestmc.neptunecarbonbungee

import club.crestmc.neptunecarbonbungee.commands.CarbonBungeeCommand
import club.crestmc.neptunecarbonbungee.commands.ServerInfoCommand
import club.crestmc.neptunecarbonbungee.config.ConfigManager
import club.crestmc.neptunecarbonbungee.database.DatabaseManager
import club.crestmc.neptunecarbonbungee.listeners.BanListeners
import club.crestmc.neptunecarbonbungee.listeners.PingListener
import club.crestmc.neptunecarbonbungee.listeners.UsersListeners
import club.crestmc.neptunecarbonbungee.utils.ChatUtil
import co.aikar.commands.BungeeCommandManager
import co.aikar.commands.MessageType
import dev.demeng.sentinel.wrapper.SentinelClient
import dev.demeng.sentinel.wrapper.exception.*
import dev.demeng.sentinel.wrapper.exception.unchecked.UnauthorizedException
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.plugin.Plugin
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashSet


class NeptuneCarbonBungee: Plugin() {

    var manager = BungeeCommandManager(this)
    var configManager = ConfigManager(this)
    var databaseManager = DatabaseManager(this)

    lateinit var bannedPlayers: MutableSet<UUID>

    fun getPlugin(): Plugin {
        return this
    }
    override fun onEnable() {
        initAcf()

        databaseManager.mongoConnect()

        databaseManager.serverStatusCollection.drop()

        bannedPlayers = HashSet<UUID>()

        proxy.scheduler.schedule(this, Runnable {
            proxy.logger.info(ChatUtil.translate("&eSuccessfully loaded &dCarbon Bungee &elicensed under &d" + Constants.serverName + "&e. If this plugin is being used on a server that is not &d" + Constants.serverName + "&e, please contact &d" + Constants.instanceSupervisor + " &eimmediately."))
        }, 10, TimeUnit.SECONDS)

        proxy.pluginManager.registerListener(this, BanListeners(this))
        proxy.pluginManager.registerListener(this, UsersListeners(this))
        proxy.pluginManager.registerListener(this, PingListener(this))
    }

    override fun onDisable() {
        proxy.logger.info(ChatUtil.translate("&dNeptune Carbon &ehas been shut down."))
    }

    private fun initAcf() {

        when (configManager.config?.getString("preset")) {
            "crest" -> {
                manager.setFormat(MessageType.HELP, ChatColor.DARK_AQUA, ChatColor.WHITE, ChatColor.AQUA)
                manager.setFormat(MessageType.SYNTAX, ChatColor.DARK_AQUA, ChatColor.AQUA, ChatColor.WHITE)
            }

            "minemen" -> {
                manager.setFormat(MessageType.HELP, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, ChatColor.YELLOW)
                manager.setFormat(MessageType.SYNTAX, ChatColor.YELLOW, ChatColor.LIGHT_PURPLE, ChatColor.WHITE)
            }

            "invaded" -> {
                manager.setFormat(MessageType.HELP, ChatColor.GOLD, ChatColor.GOLD, ChatColor.YELLOW)
                manager.setFormat(MessageType.SYNTAX, ChatColor.YELLOW, ChatColor.GOLD, ChatColor.WHITE)
            }

            else -> {}
        }

        manager.enableUnstableAPI("help")

        manager.registerCommand(CarbonBungeeCommand())

        manager.registerCommand(ServerInfoCommand())

        return;
    }

    private fun lVerify() {
        val sentinel: SentinelClient = SentinelClient("http://onepickbemonkey.boredsmp.club:2007/api/v1", "naa9a8hpo7i2ld17r941u6g7nt")

        var success = false
        // Get the platform controller.
        try {
            sentinel.licenseController.auth(configManager.config?.getString("license-key"), "cobalt", null, null, configManager.config?.getString("serverName")!!, SentinelClient.getCurrentIp())
            success = true
        } catch (e: InvalidLicenseException) {
            InternalErrors(this).throwFatalStartupError("The provided license key is invalid.")
        } catch (e: ExpiredLicenseException) {
            InternalErrors(this).throwFatalStartupError("The provided license has expired.")
        } catch (e: BlacklistedLicenseException) {
            logger.info(" ")
            logger.info(" ")
            logger.info("=== LICENSE BLACKLISTED ===")
            logger.info("Your license has been blacklisted. More information can be found below.")
            logger.info("Reason: " + e.blacklist.reason)
            logger.info("Timestamp: " + e.blacklist.timestamp)
            logger.info("The plugin will now shut down.")
            logger.info("Please contact support if you believe this is false.")
            logger.info("===========================")
            logger.info(" ")
            logger.info(" ")
            InternalErrors(this).throwFatalStartupError("The provided license key is blacklisted.")
        } catch (e: ExcessiveServersException) {
            InternalErrors(this).throwFatalStartupError("The plugin cannot load because the server limit has been exceeded for the license.")
        } catch (e: ExcessiveIpsException) {
            InternalErrors(this).throwFatalStartupError("The plugin cannot load because the IP limit has been exceeded for the license.")
        } catch (e: InvalidProductException) {
            InternalErrors(this).throwFatalStartupError("The provided license is for a different product. Ensure you are using the correct key.")
        } catch (e: InvalidPlatformException) {
            InternalErrors(this).throwFatalStartupError("INVALID_PLATFORM_LICENSE")
        } catch (e: IOException) {
            InternalErrors(this).throwFatalStartupError("Could not connect to the license servers, please try again later.")
        } catch (e: NoSuchMethodError) {
            e.printStackTrace()
            InternalErrors(this).throwFatalStartupError("Could not parse string [Contact Developer]")
        } catch (e: UnauthorizedException) {
            InternalErrors(this).throwFatalStartupError("The license server rejected the api key. You most likely cannot fix this issue. Ensure the plugin is completely up to date, as this is most likely caused by an API key rotation.")
        }

        if(success) {
            logger.info("Your license has been verified.")
        }
    }
}