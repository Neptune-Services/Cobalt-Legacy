package club.crestmc.neptunecarbonbungee.users

import club.crestmc.neptunecarbonbungee.NeptuneCarbonBungee
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.bson.Document
import java.net.InetSocketAddress
import java.time.Instant
import java.util.*

class UserManager(val plugin: NeptuneCarbonBungee) {
    fun createUser(uuid: UUID, username: String, ip: String) {
        plugin.databaseManager.usersCollection.insertOne(Document("uuid", uuid.toString())
            .append("username", username)
            .append("ips", listOf(ip))
            .append("joins", 1)
            .append("firstJoin", Date.from(Instant.now()))
        )
    }

    fun updateUser(player: ProxiedPlayer, data: Document, addJoin: Boolean) {
        if(player.name != data.getString("username")) {
            plugin.databaseManager.usersCollection.updateOne(eq("uuid", player.uniqueId.toString()), Updates.combine(
                Updates.set("username", player.name)
            ))
        }

        if(!(data.get("ips") as List<String>).contains((player.socketAddress as InetSocketAddress).hostName)) {
            val newList: MutableList<String> = data.get("ips") as MutableList<String>
            newList.add((player.socketAddress as InetSocketAddress).hostName)
            plugin.databaseManager.usersCollection.updateOne(eq("uuid", player.uniqueId.toString()), Updates.combine(
                Updates.set("ips", newList)
            ))
        }

        if(addJoin) {
            plugin.databaseManager.usersCollection.updateOne(eq("uuid", player.uniqueId.toString()), Updates.combine(
                Updates.set("joins", data.getInteger("joins") + 1)
            ))
        }
    }
}