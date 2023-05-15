package club.crestmc.neptunecarbonbungee

import club.crestmc.neptunecarbonbungee.utils.ChatUtil

object Constants {
    var serverName = "CrestMC Testing"
    var instanceSupervisor = "Hyperfire#0298"

    fun getPermBanMsg(reason: String): String = ChatUtil.translate(
        "&c&lYour account is permanently suspended from " + serverName +
                "\n&7" +
                "\n&cReason: &7${reason}" +
                "\n&cYou cannot appeal this punishment."
    )

    fun getPermBlacklistMsg(reason: String, association: String?): String = ChatUtil.translate(
        "&c&lYou are permanently blacklisted from " + serverName +
                "\n&7" +
                "\n&cReason: &7${reason}" +
                "\n&cYou cannot appeal this punishment." +
                (if(association != null) "\n&cThis blacklist is associated with &a$association&c." else "")
    )
}