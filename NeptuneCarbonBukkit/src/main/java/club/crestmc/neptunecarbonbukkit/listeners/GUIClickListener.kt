package club.crestmc.neptunecarbonbukkit.listeners

import club.crestmc.neptunecarbonbukkit.gui.GUI
import com.cryptomorin.xseries.XMaterial
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class GUIClickListener : Listener {
    @EventHandler
    fun onInvClick(event: InventoryClickEvent) {
        if (event.currentItem == null || event.currentItem!!.type == XMaterial.AIR.parseMaterial()) return
        if (GUI.getOpenGUIs().containsKey(event.whoClicked)) {
            val gui = GUI.getOpenGUIs()[event.whoClicked]
            if (gui != null && event.clickedInventory != null && event.clickedInventory == gui.inventory) {
                event.isCancelled = true
                val button = gui.getButton(event.slot)
                if (button != null) {
                    val action = button.action
                    action?.run()
                }
            }
        }
    }
}