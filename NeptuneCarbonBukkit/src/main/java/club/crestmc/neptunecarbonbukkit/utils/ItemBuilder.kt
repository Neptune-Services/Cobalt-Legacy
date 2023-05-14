package club.crestmc.neptunecarbonbukkit.utils

import club.crestmc.neptunecarbonbukkit.utils.ChatUtil.translate
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

class ItemBuilder(var item: ItemStack, var amount: Int, var name: String, var lore: List<String>) {

    val itemStack: ItemStack
        get() {
            val item = item
            val itemMeta = item.itemMeta
            itemMeta.setDisplayName(translate(name))
            itemMeta.lore = translateLore(lore)
            item.itemMeta = itemMeta
            return item
        }

    fun give(player: Player) {
        val item = itemStack
        for (i in 0 until amount) player.inventory.addItem(item)
    }

    fun give(player: Player, slot: Int) {
        val item = itemStack
        player.inventory.setItem(slot, item)
        if (amount > 1) {
            for (i in 0 until amount) player.inventory.addItem(item)
        }
    }

    companion object {
        fun translateLore(lore: List<String>): List<String> {
            val translated: MutableList<String> = ArrayList()
            for (entry in lore) translated.add(
                translate(
                    entry
                )
            )
            return translated
        }

        fun formatLore(lore: Array<String?>): List<String> {
            return ArrayList(Arrays.asList(*lore))
        }
    }
}