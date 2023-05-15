package club.crestmc.neptunecarbonbukkit

import club.crestmc.neptunecarbonbukkit.commands.CarbonBukkitCommand
import club.crestmc.neptunecarbonbukkit.commands.punishments.create.BanCommand
import club.crestmc.neptunecarbonbukkit.commands.punishments.create.BlacklistCommand
import club.crestmc.neptunecarbonbukkit.commands.punishments.create.KickCommand
import club.crestmc.neptunecarbonbukkit.commands.punishments.create.MuteCommand
import club.crestmc.neptunecarbonbukkit.commands.punishments.info.HistCommand
import club.crestmc.neptunecarbonbukkit.commands.punishments.remove.UnbanCommand
import club.crestmc.neptunecarbonbukkit.commands.punishments.remove.UnblacklistCommand
import club.crestmc.neptunecarbonbukkit.commands.punishments.remove.UnmuteCommand
import club.crestmc.neptunecarbonbukkit.commands.ranks.ListCommand
import club.crestmc.neptunecarbonbukkit.commands.ranks.RankManager
import club.crestmc.neptunecarbonbukkit.commands.users.AltsCommand
import club.crestmc.neptunecarbonbukkit.config.ConfigManager
import club.crestmc.neptunecarbonbukkit.database.DatabaseManager
import club.crestmc.neptunecarbonbukkit.listeners.*
import club.crestmc.neptunecarbonbukkit.utils.ChatUtil
import co.aikar.commands.BukkitCommandCompletionContext
import co.aikar.commands.MessageType
import co.aikar.commands.PaperCommandManager
import com.mongodb.client.model.Filters.eq
import dev.demeng.sentinel.wrapper.SentinelClient
import dev.demeng.sentinel.wrapper.exception.BlacklistedLicenseException
import dev.demeng.sentinel.wrapper.exception.ExcessiveIpsException
import dev.demeng.sentinel.wrapper.exception.ExcessiveServersException
import dev.demeng.sentinel.wrapper.exception.ExpiredLicenseException
import dev.demeng.sentinel.wrapper.exception.InvalidLicenseException
import dev.demeng.sentinel.wrapper.exception.InvalidPlatformException
import dev.demeng.sentinel.wrapper.exception.InvalidProductException
import dev.demeng.sentinel.wrapper.exception.unchecked.UnauthorizedException
import me.clip.placeholderapi.PlaceholderAPI
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException
import java.util.UUID

class NeptuneCarbonBukkit : JavaPlugin() {

    lateinit var manager: PaperCommandManager
    lateinit var databaseManager: DatabaseManager
    lateinit var configManager: ConfigManager
    lateinit var luckPermsAPI: LuckPerms
    lateinit var rankManager: RankManager

    lateinit var vanishedPlayers: MutableSet<UUID>
    override fun onEnable() {

        manager = PaperCommandManager(this)
        configManager = ConfigManager(this)
        databaseManager = DatabaseManager(this)
        rankManager = RankManager(this)
        luckPermsAPI = LuckPermsProvider.get()

        vanishedPlayers = HashSet<UUID>()

        logger.info("Verifying license...")
        lVerify()

        initAcf()

        databaseManager.mongoConnect()

        server.pluginManager.registerEvents(GUIClickListener(), this)
        logger.info("Loaded GUI Click hook.")

        server.pluginManager.registerEvents(MuteListener(this), this)
        server.pluginManager.registerEvents(ChatFormatter(this), this)
        logger.info("Loaded mute chat and chat formatter hook.")

        if(server.pluginManager.isPluginEnabled("PlaceholderAPI")) {
            PAPIExpansion(this).register()
            logger.info("Registered PlaceholderAPI expansion.")
        } else {
            logger.info("Could not find PlaceholderAPI, skipping registering expansion.")
        }

        server.pluginManager.registerEvents(UpdateServerListeners(this), this)
        server.pluginManager.registerEvents(LuckpermsHider(this), this)

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, Runnable {
            this.logger.info(ChatUtil.translate("&eSuccessfully loaded &dCarbon Bukkit &elicensed under &d" + Constants.serverName + "&e. If this plugin is being used on a server that is not &d" + Constants.serverName + "&e, please contact &d" + Constants.instanceSupervisor + " &eimmediately."))
        }, 10)

        this.server.messenger.registerOutgoingPluginChannel(this, "BungeeCord");

        updateStatus()
    }

    override fun onDisable() {
        databaseManager.serverStatusCollection.deleteOne(eq("serverName", configManager.config?.getString("serverName")))
        this.logger.info(ChatUtil.translate("&dNeptune Carbon &ehas been shut down."))
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

            "outerworlds" -> {
                manager.setFormat(MessageType.HELP, ChatColor.DARK_GRAY, ChatColor.LIGHT_PURPLE, ChatColor.GRAY)
                manager.setFormat(
                    MessageType.SYNTAX,
                    ChatColor.GRAY,
                    ChatColor.LIGHT_PURPLE,
                    ChatColor.WHITE,
                    ChatColor.BOLD,
                    ChatColor.DARK_PURPLE,
                    ChatColor.WHITE,
                    ChatColor.DARK_GRAY
                )
            }

            else -> {}
        }

        manager.commandCompletions.registerCompletion(
            "allOnline"
        ) { c: BukkitCommandCompletionContext? ->
            val toReturn: MutableCollection<String> = HashSet()
            for (p in server.onlinePlayers) {
                toReturn.add(p.name)
            }
            toReturn
        }

        manager.enableUnstableAPI("help")

        manager.registerCommand(CarbonBukkitCommand())
        manager.registerCommand(KickCommand())
        manager.registerCommand(BanCommand())
        //manager.registerCommand(RankCommand())
        manager.registerCommand(ListCommand())
        manager.registerCommand(UnbanCommand())
        manager.registerCommand(HistCommand())
        manager.registerCommand(MuteCommand())
        manager.registerCommand(UnmuteCommand())
        manager.registerCommand(AltsCommand())
        manager.registerCommand(BlacklistCommand())
        manager.registerCommand(UnblacklistCommand())

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
            InternalErrors().throwFatalStartupError("The provided license key is invalid.")
        } catch (e: ExpiredLicenseException) {
            InternalErrors().throwFatalStartupError("The provided license has expired.")
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
            InternalErrors().throwFatalStartupError("The provided license key is blacklisted.")
        } catch (e: ExcessiveServersException) {
            InternalErrors().throwFatalStartupError("The plugin cannot load because the server limit has been exceeded for the license.")
        } catch (e: ExcessiveIpsException) {
            InternalErrors().throwFatalStartupError("The plugin cannot load because the IP limit has been exceeded for the license.")
        } catch (e: InvalidProductException) {
            InternalErrors().throwFatalStartupError("The provided license is for a different product. Ensure you are using the correct key.")
        } catch (e: InvalidPlatformException) {
            InternalErrors().throwFatalStartupError("INVALID_PLATFORM_LICENSE")
        } catch (e: IOException) {
            InternalErrors().throwFatalStartupError("Could not connect to the license servers, please try again later.")
        } catch (e: NoSuchMethodError) {
            e.printStackTrace()
            InternalErrors().throwFatalStartupError("Could not parse string [Contact Developer]")
        } catch (e: UnauthorizedException) {
            InternalErrors().throwFatalStartupError("The license server rejected the api key. You most likely cannot fix this issue. Ensure the plugin is completely up to date, as this is most likely caused by an API key rotation.")
        }

        if(success) {
            logger.info("Your license has been verified.")
        }
    }

    fun updateStatus() {
        Bukkit.getScheduler().scheduleAsyncDelayedTask(this, Runnable {
            updateOnce()

            updateStatus()
        }, 20 * 5)
    }

    fun updateOnce() {
        databaseManager.serverStatusCollection.deleteOne(eq("serverName", configManager.config?.getString("serverName")))

        databaseManager.serverStatusCollection.insertOne(Document("serverName", configManager.config?.getString("serverName")?.lowercase())
            .append("whitelisted", server.hasWhitelist()).append("lastChecked", System.currentTimeMillis() / 1000)
            .append("playercountFiltered", server.onlinePlayers.filter {
                !vanishedPlayers.contains(it.uniqueId)
            }.size)
            .append("playercountTotal", server.onlinePlayers.size)
            .append("maxplayers", server.maxPlayers)
        )
        this.logger.info("PLAYERS UPDATED: ${server.onlinePlayers.size}")
    }
}