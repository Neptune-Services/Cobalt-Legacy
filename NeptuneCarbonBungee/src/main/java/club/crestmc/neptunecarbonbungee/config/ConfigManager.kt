package club.crestmc.neptunecarbonbungee.config

import club.crestmc.neptunecarbonbungee.NeptuneCarbonBungee
import com.google.common.io.ByteStreams
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.util.logging.Level

// what the shit is this code
class ConfigManager(private val plugin: NeptuneCarbonBungee) {
    var config: Configuration? = null

    var languageConfig: Configuration? = null
    private val configFile: File
    private val languageFile: File

    init {
        configFile = File(plugin.dataFolder, "config.yml")
        this.languageFile = File(plugin.dataFolder, "language.yml")
        load()
    }

    fun load() {
        if (!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdir()
        }
        try {
            var inputStream: InputStream
            var outputStream: OutputStream
            if (!configFile.exists()) {
                inputStream = javaClass.classLoader.getResourceAsStream("config.yml")
                outputStream = Files.newOutputStream(configFile.toPath())
                ByteStreams.copy(inputStream, outputStream)
            }
            config = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(configFile)

            if (!languageFile.exists()) {
                inputStream = javaClass.classLoader.getResourceAsStream("language.yml")
                outputStream = Files.newOutputStream(languageFile.toPath())
                ByteStreams.copy(inputStream, outputStream)
            }
            languageConfig = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(languageFile)
        } catch (e: IOException) {
            plugin.logger.log(Level.SEVERE, "An error occurred whilst loading the configuration files", e)
        }
    }

    fun saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(config, configFile)
        } catch (e: IOException) {
            plugin.logger.log(Level.SEVERE, "An error occurred whilst saving the configuration file", e)
        }
    }

    fun reloadConfig() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(configFile)
        } catch (e: IOException) {
            plugin.logger.log(Level.SEVERE, "An error occurred whilst reload the configuration file", e)
        }
    }

    fun reloadLanguage() {
        try {
            languageConfig = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(languageFile)
        } catch (e: IOException) {
            plugin.logger.log(Level.SEVERE, "An error occurred whilst reload the configuration file", e)
        }
    }
}