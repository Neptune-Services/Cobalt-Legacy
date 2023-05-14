package club.crestmc.neptunecarbonbukkit.database

import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.eq
import org.bson.Document

class DatabaseManager(private val plugin: NeptuneCarbonBukkit) {

    val url = plugin.configManager.config?.getString("databaseUrl")

    lateinit var database: MongoDatabase
    lateinit var punishmentsCollection: MongoCollection<Document>
    lateinit var grantsCollection: MongoCollection<Document>
    lateinit var usersCollection: MongoCollection<Document>
    lateinit var serverStatusCollection: MongoCollection<Document>
    lateinit var mongoClient: MongoClient

    fun mongoConnect() {
        mongoClient = MongoClients.create(url!!)

        database = mongoClient.getDatabase("NeptuneCobalt")
        punishmentsCollection = database.getCollection("punishments")
        grantsCollection = database.getCollection("grants")
        usersCollection = database.getCollection("users")
        serverStatusCollection = database.getCollection("serverinfo")
    }
}