package club.crestmc.neptunecarbonbukkit.utils

import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit
import club.crestmc.neptunecarbonbukkit.entities.UnknownPlayer
import com.mongodb.BasicDBObject
import com.mongodb.client.MongoCursor
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import org.bson.Document
import java.util.Arrays
import java.util.LinkedList

class PlayerUtils(val plugin: NeptuneCarbonBukkit) {
    fun getAlts(target: UnknownPlayer): List<UnknownPlayer> {
        val user = plugin.databaseManager.usersCollection.find(eq("uuid", target.uuid.toString())).first()
        val users = plugin.databaseManager.usersCollection.find(eq("ips", BasicDBObject("\$in", user.get("ips") as MutableList<String>)))

        val array: MutableList<UnknownPlayer> = LinkedList<UnknownPlayer>()

        val cursor: MongoCursor<Document> = users.iterator()

        while(cursor.hasNext()) {
            val doc = cursor.next()
            array.add(UUIDUtil.getUnknownPlayerDatabaseFromUuid(doc.getString("uuid"))!!)
        }
        return array
    }

    fun formatAlts(altsList: List<UnknownPlayer>): List<String> {
        val docs = plugin.databaseManager.punishmentsCollection.find(eq("active", true)).filter(eq("active", true))

        return altsList.map {
            if(docs.filter(and(eq("type", "blacklist"), eq("active", true), eq("uuid", it.uuid.toString()))).first() != null)
                "&4${it.username}"
            else if(docs.filter(and(eq("type", "ban"), eq("active", true), eq("uuid", it.uuid.toString()))).first() != null)
                "&c${it.username}"
            else if(docs.filter(and(eq("type", "mute"), eq("active", true), eq("uuid", it.uuid.toString()))).first() != null)
                "&6${it.username}"
            else if(plugin.server.getPlayer(it.username!!) != null)
                "&a${it.username}"
            else
                "&7${it.username}"
        }
    }

    fun getBanEvasion(altsList: List<UnknownPlayer>, exclude: UnknownPlayer): String? {
        val docs = plugin.databaseManager.punishmentsCollection.find(eq("active", true)).filter(eq("active", true))

        var username: String? = null

        for(it: UnknownPlayer in altsList) {
            if(it != exclude) {
                if(docs.filter(and(eq("type", "blacklist"), eq("active", true), eq("uuid", it.uuid.toString()))).first() == null) {
                    if(docs.filter(and(eq("type", "ban"), eq("active", true), eq("uuid", it.uuid.toString()))).first() != null)
                        username = it.username
                }
            }
        }

        return username
    }
}