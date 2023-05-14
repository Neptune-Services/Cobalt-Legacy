package club.crestmc.neptunecarbonbukkit.commands.ranks

import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit
import club.crestmc.neptunecarbonbukkit.utils.ChatUtil
import club.crestmc.neptunecarbonbukkit.utils.ColorUtil
import club.crestmc.neptunecarbonbukkit.utils.TimeUtil.getDurationFrom
import co.aikar.commands.*
import co.aikar.commands.annotation.*
import net.luckperms.api.context.DefaultContextKeys
import net.luckperms.api.model.group.Group
import net.luckperms.api.model.user.User
import net.luckperms.api.model.user.UserManager
import net.luckperms.api.node.Node
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.time.Duration
import java.time.Instant
import java.util.concurrent.CompletableFuture


@CommandAlias("rank|group")
@Description("Manage ranks/groups.")
class RankCommand : BaseCommand() {
    @Dependency
    lateinit var plugin: NeptuneCarbonBukkit

    @Subcommand("set")
    @Description("Set the player's rank. Warning: This overrides any sub-ranks or parents the target already has!")
    @CommandPermission("carbon.rank.set")
    @CommandCompletion("@allPlayers")
    fun onRankSetCommand(issuer: CommandIssuer,
                         @Name("target") targetArg: String,
                         @Name("rank") rankArg: String,
                         @Name("reason") reasonArg: String
    ) {

        val target: OfflinePlayer = plugin.server.getOfflinePlayer(targetArg)
        if (target == null) {
            plugin.manager
                .sendMessage(issuer, MessageType.ERROR, MessageKeys.COULD_NOT_FIND_PLAYER, "{search}", targetArg)
            return
        }

        val group: Group? = plugin.luckPermsAPI.groupManager.getGroup(rankArg)
        if(group == null) {
            issuer.sendMessage(ChatUtil.getLanguageTranslation("ranks.errors.group_not_found"))
            return
        }

        val userManager: UserManager = plugin.luckPermsAPI.getUserManager()
        val userFuture: CompletableFuture<User> = userManager.loadUser(target.uniqueId)

        userFuture.thenAcceptAsync { user ->
            val status = plugin.rankManager.setRank(group.name, target, user)

            when(status) {
                "SUCCESS" -> {
                    issuer.sendMessage(ChatUtil.translate(
                        ChatUtil.getLanguageTranslation("ranks.success_set")
                            .replace("%player%", ColorUtil().getColoredNamePlayer(target))
                            .replace("%rank%", group.displayName?:group.name)
                    ))
                    System.out.println(target.isOnline)
                    if(target.isOnline) {
                        System.out.println(target.isOnline)
                        (target as Player).sendMessage(ChatUtil.translate(
                            ChatUtil.getLanguageTranslation("ranks.given.parent")
                                .replace("%rank%", group.displayName?:group.name)
                        ))
                        System.out.println("debug2")
                    }
                }
            }
        }
    }

    @HelpCommand
    @CatchUnknown
    fun onHelp(issuer: CommandIssuer, help: CommandHelp) {
        help.showHelp()
    }
}