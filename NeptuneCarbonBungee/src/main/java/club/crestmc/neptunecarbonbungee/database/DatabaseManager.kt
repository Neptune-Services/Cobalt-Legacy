package club.crestmc.neptunecarbonbungee.database

import club.crestmc.neptunecarbonbungee.NeptuneCarbonBungee
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.eq
import org.bson.Document

class DatabaseManager(private val plugin: NeptuneCarbonBungee) {

    private val url = plugin.configManager.config?.getString("databaseUrl")!!

    lateinit var database: MongoDatabase
    lateinit var punishmentsCollection: MongoCollection<Document>
    lateinit var serverStatusCollection: MongoCollection<Document>
    lateinit var usersCollection: MongoCollection<Document>
    lateinit var globalWhitelistCollection: MongoCollection<Document>
    lateinit var mongoClient: MongoClient

    fun mongoConnect() {
        mongoClient = MongoClients.create(url)

        database = mongoClient.getDatabase("NeptuneCobalt")
        punishmentsCollection = database.getCollection("punishments")
        serverStatusCollection = database.getCollection("serverinfo")
        globalWhitelistCollection = database.getCollection("globalWhitelist")
        usersCollection = database.getCollection("users")
    }
}