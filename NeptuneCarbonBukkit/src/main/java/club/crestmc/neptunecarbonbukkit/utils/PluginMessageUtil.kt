package club.crestmc.neptunecarbonbukkit.utils

import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit
import com.google.common.collect.Iterables
import com.google.common.io.ByteStreams
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

object PluginMessageUtil {
    /**
     * Sends a plugin message to the specified channel
     * @param channel The channel to send the plugin message to
     * @param data The data to send in the plugin message
     */
    fun sendData(channel: String?, vararg data: String?) {
        println("SENDING EMSSAGES")
        val output = ByteStreams.newDataOutput()
        for (message in data) {
            output.writeUTF(message)
        }
        val player: Player? = Iterables.getFirst(Bukkit.getOnlinePlayers(), null)
        if (player != null) {
            if (channel != null) {
                player.sendPluginMessage(JavaPlugin.getPlugin(NeptuneCarbonBukkit::class.java), channel, output.toByteArray())
            }
        }
    }
}