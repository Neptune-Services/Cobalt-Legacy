package club.crestmc.neptunecarbonbukkit.punishmenthistory

import club.crestmc.neptunecarbonbukkit.Constants.primaryColor
import club.crestmc.neptunecarbonbukkit.Constants.secondaryColor
import club.crestmc.neptunecarbonbukkit.commands.punishments.PunishmentType
import club.crestmc.neptunecarbonbukkit.entities.UnknownPlayer
import club.crestmc.neptunecarbonbukkit.gui.Button
import club.crestmc.neptunecarbonbukkit.gui.CustomGUI
import club.crestmc.neptunecarbonbukkit.gui.GUI
import club.crestmc.neptunecarbonbukkit.utils.ChatUtil.translate
import club.crestmc.neptunecarbonbukkit.utils.ColorUtil
import club.crestmc.neptunecarbonbukkit.utils.ItemBuilder
import club.crestmc.neptunecarbonbukkit.utils.ItemBuilder.Companion.formatLore
import club.crestmc.neptunecarbonbukkit.utils.Util.Companion.getExpirationDate
import com.cryptomorin.xseries.XMaterial
import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCursor
import org.bson.Document
import org.bukkit.entity.Player
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashSet

object HistoryUtils {
    fun createPages(documents: FindIterable<Document>, type: PunishmentType): LinkedHashSet<PunishmentsPage>? {
        System.out.println("MAKING THE FRICKING PAGES HOLY POOP")
        val set: MutableSet<PunishmentsPage> = LinkedHashSet()
        val tempSet: MutableSet<Document> = LinkedHashSet()

        val cursor: MongoCursor<Document> = documents.iterator()

        if (documents.filter(Document("type", type.toString().lowercase(Locale.getDefault()))).first() != null) {
            var loop = -1

            while(cursor.hasNext()) {
                val punishment = cursor.next()
                println(punishment)

                System.out.println("${punishment.getString("type")} IS MAYBE ${type.toString().lowercase()}")

                if (punishment.getString("type").trim() == type.toString().lowercase().trim()) {
                    System.out.println("${punishment.getString("type")} IS ${type.toString().lowercase()} CONFIRMED 2023 OMG REAL")
                    loop++
                    tempSet.add(punishment)

                    if(tempSet.size >= 9) {
                        System.out.println("")
                        System.out.println("ADDING PAGE BC FULL")
                        System.out.println("")
                        val page = PunishmentsPage()
                        for (doc: Document in tempSet) {
                            page.punishments.add(doc)
                        }
                        set.add(page)
                        tempSet.clear()
                    }
                }
            }

            if(!tempSet.isEmpty()) {
                val page = PunishmentsPage()
                for (doc: Document in tempSet) {
                    page.punishments.add(doc)
                }
                System.out.println("")
                System.out.println("ADDING PAGE BC UH NOT FULL ${page}")
                System.out.println("")
                set.add(page)
                tempSet.clear()
            }
            return set as LinkedHashSet<PunishmentsPage>
        } else return null
    }
}