package club.crestmc.neptunecarbonbukkit

import club.crestmc.neptunecarbonbukkit.utils.TimeUtil
import com.mongodb.BasicDBObject
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer

class PAPIExpansion(val plugin: NeptuneCarbonBukkit) : PlaceholderExpansion() {
    override fun getAuthor(): String {
        return "Hyperfire"
    }

    override fun getIdentifier(): String {
        return "cobalt"
    }

    override fun getVersion(): String {
        return "1.0.0"
    }

    override fun persist(): Boolean {
        return true
    }

    override fun onRequest(player: OfflinePlayer, params: String): String? {
        if(params.equals("isBanned", ignoreCase = true)) {
            val dbCheck = plugin.databaseManager.punishmentsCollection.find(and(
                eq("uuid", player.uniqueId.toString()),
                eq("type", "ban"),
                eq("active", true)
            ))

            return if(dbCheck.first() != null) {
                "TRUE"
            } else {
                "FALSE"
            }
        }

        if(params.equals("isBlacklisted", ignoreCase = true)) {
            val user = plugin.databaseManager.usersCollection.find(eq("uuid", player.uniqueId.toString())).first()
            val dbCheck = plugin.databaseManager.punishmentsCollection.find(and(
                eq("type", "blacklist"),
                eq("ips", BasicDBObject("\$in", user.get("ips") as MutableList<String>)),
                eq("active", true)
            ))

            return if(dbCheck.first() != null) {
                "TRUE"
            } else {
                "FALSE"
            }
        }

        if(params.equals("banReason", ignoreCase = true)) {
            val dbCheck = plugin.databaseManager.punishmentsCollection.find(and(
                eq("uuid", player.uniqueId.toString()),
                eq("type", "ban"),
                eq("active", true)
            ))

            return if(dbCheck.first() != null) {
                dbCheck.first().getString("reason")
            } else {
                "None"
            }
        }

        if(params.equals("blacklistReason", ignoreCase = true)) {
            val user = plugin.databaseManager.usersCollection.find(eq("uuid", player.uniqueId.toString())).first()
            val dbCheck = plugin.databaseManager.punishmentsCollection.find(and(
                eq("type", "blacklist"),
                eq("ips", BasicDBObject("\$in", user.get("ips") as MutableList<String>)),
                eq("active", true)
            ))

            return if(dbCheck.first() != null) {
                dbCheck.first().getString("reason")
            } else {
                "None"
            }
        }

        if(params.equals("blacklistAssociation", ignoreCase = true)) {
            val user = plugin.databaseManager.usersCollection.find(eq("uuid", player.uniqueId.toString())).first()
            val dbCheck = plugin.databaseManager.punishmentsCollection.find(and(
                eq("type", "blacklist"),
                eq("ips", BasicDBObject("\$in", user.get("ips") as MutableList<String>)),
                eq("active", true)
            ))

            return if(dbCheck.first().getString("uuid") != player.uniqueId.toString()) plugin.databaseManager.usersCollection.find(eq("uuid", dbCheck.first().getString("uuid"))).first().getString("username")
            else "None"
        }

        if(params.equals("banExpiry", ignoreCase = true)) {
            val dbCheck = plugin.databaseManager.punishmentsCollection.find(and(
                eq("uuid", player.uniqueId.toString()),
                eq("type", "ban"),
                eq("active", true)
            ))

            return if(dbCheck.first() != null) {
                if(dbCheck.first().getDate("expiry") != null) {
                    TimeUtil.getHowLongUntil(dbCheck.first().getDate("expiry").time)
                } else {
                    "Permanent"
                }
            } else {
                "None"
            }
        }

        if(params.equals("blacklistExpiry", ignoreCase = true)) {
            val user = plugin.databaseManager.usersCollection.find(eq("uuid", player.uniqueId.toString())).first()
            val dbCheck = plugin.databaseManager.punishmentsCollection.find(and(
                eq("type", "blacklist"),
                eq("ips", BasicDBObject("\$in", user.get("ips") as MutableList<String>)),
                eq("active", true)
            ))

            return if(dbCheck.first() != null) {
                if(dbCheck.first().getDate("expiry") != null) {
                    TimeUtil.getHowLongUntil(dbCheck.first().getDate("expiry").time)
                } else {
                    "Permanent"
                }
            } else {
                "None"
            }
        }

        return null
    }
}