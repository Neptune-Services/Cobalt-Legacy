package club.crestmc.neptunecarbonbukkit.utils;

import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public final class InventoryUtil {

    public static ItemStack[] fixInventoryOrder(ItemStack[] source) {
        final ItemStack[] fixed = new ItemStack[36];

        System.arraycopy(source, 0, fixed, 27, 9);
        System.arraycopy(source, 9, fixed, 0, 27);

        return fixed;
    }

    public static boolean clickedTopInventory(InventoryDragEvent event) {
        final InventoryView view = event.getView();
        final Inventory topInventory = view.getTopInventory();

        if (topInventory == null) {
            return false;
        } else {
            boolean result = false;
            final int size = topInventory.getSize();

            for (Integer entry : event.getNewItems().keySet()) {
                if (entry >= size) continue;
                result = true;

                break;
            }

            return result;
        }
    }
}
