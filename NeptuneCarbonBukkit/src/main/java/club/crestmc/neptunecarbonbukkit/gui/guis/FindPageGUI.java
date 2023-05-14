package club.crestmc.neptunecarbonbukkit.gui.guis;

import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit;
import club.crestmc.neptunecarbonbukkit.commands.punishments.PunishmentType;
import club.crestmc.neptunecarbonbukkit.entities.UnknownPlayer;
import club.crestmc.neptunecarbonbukkit.gui.Button;
import club.crestmc.neptunecarbonbukkit.gui.CustomGUI;
import club.crestmc.neptunecarbonbukkit.gui.GUI;
import club.crestmc.neptunecarbonbukkit.utils.ColorUtil;
import club.crestmc.neptunecarbonbukkit.utils.ItemBuilder;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class FindPageGUI extends CustomGUI {

    private final NeptuneCarbonBukkit plugin;

    public FindPageGUI(Player player, int size, String title) {
        super(player, size, title);
        plugin = NeptuneCarbonBukkit.getPlugin(NeptuneCarbonBukkit.class);
    }

    public void setup(String cl, PunishmentType type, UnknownPlayer target, int pages) {
        ItemBuilder itemBuilder = new ItemBuilder(XMaterial.BOOK.parseItem(), 1, "&4&lNULL", new ArrayList<>());

        for (int i = 0; i < pages; i++) {
            int page = i;
            gui.setButton(i, new Button(
                    itemBuilder.getItem(),
                    () -> {
                        GUI.close(gui);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                switch (cl.toLowerCase()) {
                                    case "punishments":
                                        CasesDetailed detailedPunishmentHistoryGUI = new CasesDetailed(getGui().getPlayer(), 27, new ColorUtil().getColoredNameFromUuid(target.getUuid()) + "&2's " + type.toString().toLowerCase() + "");
                                        detailedPunishmentHistoryGUI.setup(type, getGui().getPlayer(), target, (page + 1));
                                        GUI.open(CasesDetailed.getGui());
                                        break;
                                }
                            }
                        }.runTaskLater(plugin, 1);
                    },
                    "&a&lPage " + (i + 1),
                    itemBuilder.getLore()
            ));
        }
    }
}
