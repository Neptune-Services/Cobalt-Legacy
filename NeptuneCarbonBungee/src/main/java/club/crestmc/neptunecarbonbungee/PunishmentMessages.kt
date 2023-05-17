package club.crestmc.neptunecarbonbungee

import club.crestmc.neptunecarbonbungee.utils.ChatUtil

class PunishmentMessages(val plugin: NeptuneCarbonBungee) {
    var appealLink: String? = plugin.configManager.config?.getString("banAppealLink")
    var blacklistAppealLink: String? = plugin.configManager.config?.getString("blacklistAppealLink")
    var buyUnbanLink: String? = plugin.configManager.config?.getString("unbanPurchaseLink")
    var buyUnblacklistLink: String? = plugin.configManager.config?.getString("unblacklistPurchaseLink")

    fun getPermBanMsg(reason: String): String = ChatUtil.translate(
        "&cYou are permanently &4banned&c from ${Constants.serverName}." +
                "\n&cYou were &4banned&c for: &7$reason" +
                (if(appealLink != "" && appealLink != null) "\n&7If you believe this punishment was unjustified, you may submit an appeal at $appealLink." else "&7You may not appeal this type of punishment.") +
                (if(buyUnbanLink != "" && buyUnbanLink != null) "\n&6You may also purchase an unban at $buyUnbanLink" else "&4You may not purchase an unban for this type of ban.")
    )

    fun getPermBlacklistMsg(reason: String, association: String?): String = ChatUtil.translate(
        "&cYou are permanently &4blacklisted&c from ${Constants.serverName}." +
                (if(association != "" && association != null) "\n&cYour blacklist is in relation to the account: &a$association" else "") +
                "\n&cYou were &4blacklisted&c for: &7$reason" +
                (if(blacklistAppealLink != "" && blacklistAppealLink != null) "\n&7If you believe this punishment was unjustified, you may submit an appeal ${if(association != null) "for &a$association&7 " else ""}at $blacklistAppealLink." else "\n&7You may not appeal this type of punishment.") +
                (if(buyUnblacklistLink != "" && buyUnblacklistLink != null) "\n&6You may also purchase an unban at $buyUnblacklistLink" else "\n&4You may not purchase an unban for this type of ban.")
    )

    fun getPermMuteMsg(reason: String): String = ChatUtil.translate("&c&lYou cannot speak as you are permanently muted from " + Constants.serverName + "." +
            "\n&7" +
            "\n&cReason: &7${reason}" +
            "\n&cYou cannot appeal this punishment."
    )
}