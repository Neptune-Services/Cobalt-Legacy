package club.crestmc.neptunecarbonbukkit.commands.ranks

import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit
import net.luckperms.api.context.DefaultContextKeys
import net.luckperms.api.model.group.Group
import net.luckperms.api.model.user.User
import net.luckperms.api.node.Node
import net.luckperms.api.node.NodeBuilder
import net.luckperms.api.node.NodeType
import net.luckperms.api.node.types.InheritanceNode
import net.luckperms.api.query.QueryOptions
import org.bukkit.OfflinePlayer

class RankManager(val plugin: NeptuneCarbonBukkit) {
    fun setRank(name: String, player: OfflinePlayer, user: User): String {
        val group: Group? = plugin.luckPermsAPI.groupManager.getGroup(name)
        if(group == null) {
            return "INVALID_RANK"
        }

        for (g: Group in user.getInheritedGroups(QueryOptions.defaultContextualOptions())) {
            user.data().remove(Node.builder("group." + g.name).build())
        }
        user.data().add(Node.builder("group." + group.name).build())

        plugin.luckPermsAPI.userManager.saveUser(user)

        return "SUCCESS"
    }
}