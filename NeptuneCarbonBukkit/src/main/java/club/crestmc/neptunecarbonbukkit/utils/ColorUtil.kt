package club.crestmc.neptunecarbonbukkit.utils

import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit
import club.crestmc.neptunecarbonbukkit.utils.UUIDUtil.getName
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.User
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.concurrent.CompletableFuture

class ColorUtil {
    var plugin = JavaPlugin.getPlugin(
        NeptuneCarbonBukkit::class.java
    )

    fun getColoredNameFromUuid(uuid: UUID): String {
        val player: OfflinePlayer = plugin.server.getOfflinePlayer(uuid)
        val toReturn: String = getColoredName(uuid) ?: ("[UNKNOWN] " + UUIDUtil.getName(uuid))

        return ChatUtil.translate(toReturn)
    }

    fun getColoredNamePlayer(player: OfflinePlayer): String {
        val userFuture: CompletableFuture<User> = plugin.luckPermsAPI.userManager.loadUser(player.uniqueId)

        var toReturn: String = player.name!!
        userFuture.thenAcceptAsync { user ->
            toReturn = getColor(player.uniqueId) + player.name
        }
        return ChatUtil.translate(toReturn)
    }

    fun getColoredName(uuid: UUID?): String? {
        return if (Bukkit.getPlayer(uuid!!) != null) {
            getColor(uuid) + Bukkit.getPlayer(uuid)!!.name
        } else {
            getColor(uuid) + getName(uuid)
        }
    }

    fun getColor(uuid: UUID?): String? {
        var color = ChatColor.GRAY.toString() + ""
        val luckPerms = LuckPermsProvider.get()
        val groupManager = luckPerms.groupManager
        val userManager = luckPerms.userManager
        val user = userManager.getUser(uuid!!) ?: return color
        val primaryGroup = groupManager.getGroup(user.primaryGroup)
        color = primaryGroup!!.cachedData.metaData.getMetaValue("color") ?: "&7"
        return color
    }
}