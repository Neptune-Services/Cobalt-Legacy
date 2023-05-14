package club.crestmc.neptunecarbonbukkit.commands.ranks

import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit
import club.crestmc.neptunecarbonbukkit.utils.ChatUtil
import club.crestmc.neptunecarbonbukkit.utils.ColorUtil
import club.crestmc.neptunecarbonbukkit.utils.RankUtil
import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandIssuer
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Dependency
import co.aikar.commands.annotation.Description
import org.bukkit.entity.Player
import java.util.Comparator
import kotlin.streams.toList

@CommandAlias("list|who")
@Description("Show a list of all online players.")
class ListCommand : BaseCommand() {
    @Dependency
    lateinit var plugin: NeptuneCarbonBukkit

    @Default
    fun onList(issuer: CommandIssuer) {
        val players = plugin.server.onlinePlayers
            .sortedByDescending { player: Player -> RankUtil.getPriority(player.uniqueId) }
            .filter {
                plugin.server.getPlayer(issuer.uniqueId)?.canSee(it) != false
            }
            .map {
                val vanished = if(plugin.vanishedPlayers.contains(it.uniqueId)) "&7[Hidden] &7" else ""
                "${vanished}${ColorUtil().getColoredName(it.uniqueId)}"
            }
            .toList()

        val ranks = RankUtil.getOrderedRanks()

        issuer.sendMessage(ChatUtil.translate("${ranks}" +
                "\n&f(${players.size}/${plugin.server.maxPlayers}): &7${players.take(150).joinToString(separator = "&f, &7")}" +
                if(players.size >= 150) "\n&cOnly showing first 150 players." else ""
        ))
    }
}