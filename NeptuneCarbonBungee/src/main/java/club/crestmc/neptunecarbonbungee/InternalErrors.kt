package club.crestmc.neptunecarbonbungee

import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin


class InternalErrors(val plugin: NeptuneCarbonBungee) {

    fun throwFatalStartupError(msg: String) {
        plugin.proxy.logger.info(" ")
        plugin.proxy.logger.warning("==============================================================")
        plugin.proxy.logger.warning("                 A fatal error has occurred.")
        plugin.proxy.logger.warning(" ")
        plugin.proxy.logger.warning(msg)
        plugin.proxy.logger.warning("Please contact a developer if you cannot solve this issue.")
        plugin.proxy.logger.warning("==============================================================")
        plugin.proxy.logger.info(" ")

        plugin.proxy.stop("Fatal error occured")
    }
}