package club.crestmc.neptunecarbonbukkit.utils

import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit
import club.crestmc.neptunecarbonbukkit.entities.UnknownPlayer
import com.google.gson.JsonParser
import com.mongodb.client.model.Filters.eq
import net.luckperms.api.LuckPermsProvider
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import java.util.function.Consumer
import java.util.regex.Pattern

object UUIDUtil {
    private val cachedPlayers: MutableMap<String?, UUID> = HashMap()
    private val plugin: NeptuneCarbonBukkit = JavaPlugin.getPlugin(NeptuneCarbonBukkit::class.java)

    init {
        Bukkit.getScheduler().runTaskTimerAsynchronously(
            JavaPlugin.getPlugin(NeptuneCarbonBukkit::class.java),
            Runnable { cachedPlayers.clear() },
            0L,
            30 * 60 * 20L
        )
    }

    fun asyncGetUUID(username: String, consumer: Consumer<UUID?>) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable { consumer.accept(getUUID(username)) })
    }

    fun getUnknownPlayerDatabaseFromUsername(username: String): UnknownPlayer? {
        val regex: Pattern = Pattern.compile("^$username$", Pattern.CASE_INSENSITIVE)
        val check = plugin.databaseManager.usersCollection.find(eq("username", regex)).first()
        return if(check == null) {
            null
        } else {
            val up = UnknownPlayer()
            up.username = check.getString("username")
            up.uuid = UUID.fromString(check.getString("uuid"))
            up
        }
    }

    fun getUnknownPlayerDatabaseFromUuid(uuid: String): UnknownPlayer? {
        val check = plugin.databaseManager.usersCollection.find(eq("uuid", uuid)).first()
        return if(check == null) {
            null
        } else {
            val up = UnknownPlayer()
            up.username = check.getString("username")
            up.uuid = UUID.fromString(check.getString("uuid"))
            up
        }
    }

    fun getUUID(username: String): UUID? {
        return if (!cachedPlayers.containsKey(username)) {
            try {
                val apiServer = URL("https://api.mojang.com/users/profiles/minecraft/$username")
                val uuidReader = InputStreamReader(apiServer.openStream())
                val uuidString = JsonParser().parse(uuidReader).asJsonObject["id"].asString
                val uuid = UUID.fromString(
                    uuidString.replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)".toRegex(),
                        "$1-$2-$3-$4-$5"
                    )
                )
                cachedPlayers[getName(uuid)] = uuid
                uuid
            } catch (e: IOException) {
                null
            } catch (e: IllegalStateException) {
                null
            }
        } else {
            cachedPlayers[username]
        }
    }

    fun asyncGetName(uuid: UUID, consumer: Consumer<String?>) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable { consumer.accept(getName(uuid)) })
    }

    fun getName(uuid: UUID): String? {
        return if (!cachedPlayers.containsValue(uuid)) {
            try {
                val uuidString = uuid.toString().replace("-", "")
                val sessionServer = URL("https://sessionserver.mojang.com/session/minecraft/profile/$uuidString")
                val nameReader = InputStreamReader(sessionServer.openStream())
                val username = JsonParser().parse(nameReader).asJsonObject["name"].asString
                cachedPlayers[username] = uuid
                username
            } catch (e: IOException) {
                null
            } catch (e: IllegalStateException) {
                null
            }
        } else {
            cachedPlayers.keys.stream().filter { username: String? -> cachedPlayers[username] == uuid }
                .findFirst().orElse(null)
        }
    }

    fun getUnknownPlayerFromUuid(uuid: UUID): UnknownPlayer? {
        return if (!cachedPlayers.containsValue(uuid)) {
            try {
                val uuidString = uuid.toString().replace("-", "")
                val sessionServer = URL("https://sessionserver.mojang.com/session/minecraft/profile/$uuidString")
                val nameReader = InputStreamReader(sessionServer.openStream())
                val username = JsonParser().parse(nameReader).asJsonObject["name"].asString
                cachedPlayers[username] = uuid
                val up = UnknownPlayer()
                up.username = (username)
                up.uuid = (uuid)
                up
            } catch (e: IOException) {
                null
            } catch (e: IllegalStateException) {
                null
            }
        } else {
            val up = UnknownPlayer()
            up.uuid = (uuid)
            up.username = (cachedPlayers.keys.stream().filter { username: String? -> cachedPlayers[username] == uuid }
                .findFirst().orElse(null))
            up
        }
    }

    fun getUnknownPlayerFromUsername(username: String): UnknownPlayer? {
        return if (!cachedPlayers.containsKey(username)) {
            try {
                val apiServer = URL("https://api.mojang.com/users/profiles/minecraft/$username")
                val uuidReader = InputStreamReader(apiServer.openStream())
                val uuidString = JsonParser().parse(uuidReader).asJsonObject["id"].asString
                val uuid = UUID.fromString(
                    uuidString.replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)".toRegex(),
                        "$1-$2-$3-$4-$5"
                    )
                )
                val up = UnknownPlayer()
                up.uuid = (uuid)
                up.username = (getName(uuid))
                cachedPlayers[getName(uuid)] = uuid
                up
            } catch (e: IOException) {
                null
            } catch (e: IllegalStateException) {
                null
            }
        } else {
            val up = UnknownPlayer()
            up.uuid = (cachedPlayers[username])
            up.username = (username)
            up
        }
    }
}