package club.crestmc.neptunecarbonbukkit.config

import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import java.util.logging.Level

class ConfigManager(private val plugin: NeptuneCarbonBukkit) {
    var config: FileConfiguration? = null
    var languageConfig: FileConfiguration? = null
    private val configFile: File
    private val languageFile: File

    init {
        configFile = File(plugin.dataFolder, "config.yml")
        languageFile = File(plugin.dataFolder, "language.yml")
        load()
    }

    fun load() {
        if (!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdir()
        }
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false)
        }
        if (!languageFile.exists()) {
            plugin.saveResource("language.yml", false)
        }
        plugin.logger.info("Loading config.yml...")
        config = YamlConfiguration.loadConfiguration(configFile)
        plugin.logger.info("Loading language.yml...")
        languageConfig = YamlConfiguration.loadConfiguration(languageFile)
    }

    fun saveConfig() {
        try {
            config!!.save(configFile)
        } catch (e: IOException) {
            plugin.logger.log(Level.SEVERE, "An error occurred whilst saving the configuration file", e)
        }
    }

    fun saveLanguage() {
        try {
            languageConfig!!.save(languageFile)
        } catch (e: IOException) {
            plugin.logger.log(Level.SEVERE, "An error occurred whilst saving the language configuration file", e)
        }
    }

    fun reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile)
    }

    fun reloadLanguageConfig() {
        languageConfig = YamlConfiguration.loadConfiguration(languageFile)
    }
}