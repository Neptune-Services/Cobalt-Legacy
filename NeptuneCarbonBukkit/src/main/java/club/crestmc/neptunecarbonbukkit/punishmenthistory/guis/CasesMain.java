package club.crestmc.neptunecarbonbukkit.punishmenthistory.guis;

import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit;
import club.crestmc.neptunecarbonbukkit.commands.punishments.PunishmentType;
import club.crestmc.neptunecarbonbukkit.entities.UnknownPlayer;
import club.crestmc.neptunecarbonbukkit.gui.Button;
import club.crestmc.neptunecarbonbukkit.gui.CustomGUI;
import club.crestmc.neptunecarbonbukkit.gui.GUI;
import club.crestmc.neptunecarbonbukkit.gui.guis.CasesDetailed;
import club.crestmc.neptunecarbonbukkit.utils.ColorUtil;
import club.crestmc.neptunecarbonbukkit.utils.ItemBuilder;
import com.cryptomorin.xseries.XMaterial;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class CasesMain extends CustomGUI {
    private final NeptuneCarbonBukkit plugin;
    public CasesMain(Player player, int size, String title) {
        super(player, size, title);
        plugin = NeptuneCarbonBukkit.getPlugin(NeptuneCarbonBukkit.class);
    }

    public void setup(Player player, UnknownPlayer target) {

        String coloredName = new ColorUtil().getColoredNameFromUuid(target.getUuid());

        gui.setFiller(9);

        ItemBuilder mutes = new ItemBuilder(XMaterial.BEDROCK.parseItem(), 1, "&c&lPlayer has no mutes.", new ArrayList<>());
        ItemBuilder kicks = new ItemBuilder(XMaterial.BEDROCK.parseItem(), 1, "&c&lPlayer has no kicks.", new ArrayList<>());
        ItemBuilder bans = new ItemBuilder(XMaterial.BEDROCK.parseItem(), 1, "&c&lPlayer has no bans.", new ArrayList<>());
        ItemBuilder blacklists = new ItemBuilder(XMaterial.BEDROCK.parseItem(), 1, "&c&lPlayer has no blacklists.", new ArrayList<>());

        Button muteButton = new Button(mutes.getItem(), mutes.getName(), mutes.getLore());
        Button kickButton = new Button(kicks.getItem(), kicks.getName(), kicks.getLore());
        Button banButton = new Button(bans.getItem(), bans.getName(), bans.getLore());
        Button blacklistButton = new Button(blacklists.getItem(), blacklists.getName(), blacklists.getLore());

        Document check = new Document().append("uuid", target.getUuid().toString());

        FindIterable<Document> allCases = plugin.getDatabaseManager().punishmentsCollection.find(check);
        if(allCases.filter(new Document("type", "mute").append("uuid", target.getUuid().toString())).first() != null) {
            muteButton.setItem(XMaterial.ORANGE_WOOL.parseItem());
            muteButton.setName(coloredName + "&6's Mutes");
            List<Document> countList = new ArrayList<>();
            allCases.filter(new Document("type", "mute").append("uuid", target.getUuid().toString())).into(countList);
            muteButton.setAmount(countList.size());
            muteButton.setAction(() -> {
                GUI.close(gui);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        CasesAlternative detailedPunishmentHistoryGUI = new CasesAlternative(player, 27, coloredName + "&2's Mutes", plugin, target, PunishmentType.MUTE);
                        detailedPunishmentHistoryGUI.setup(1);
                        GUI.open(detailedPunishmentHistoryGUI.getGui());
                    }
                }.runTaskLater(plugin, 1);
            });
        }
        if(allCases.filter(new Document("type", "kick").append("uuid", target.getUuid().toString())).first() != null) {
            kickButton.setItem(XMaterial.YELLOW_WOOL.parseItem());
            kickButton.setName(coloredName + "&e's Kicks");
            List<Document> countList = new ArrayList<>();
            allCases.filter(new Document("type", "kick").append("uuid", target.getUuid().toString())).into(countList);
            kickButton.setAmount(countList.size());
            kickButton.setAction(() -> {
                GUI.close(gui);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        CasesAlternative detailedPunishmentHistoryGUI = new CasesAlternative(player, 27, coloredName + "&2's Kicks", plugin, target, PunishmentType.KICK);
                        detailedPunishmentHistoryGUI.setup(1);
                        GUI.open(detailedPunishmentHistoryGUI.getGui());
                    }
                }.runTaskLater(plugin, 1);
            });
        }
        if(allCases.filter(new Document("type", "ban").append("uuid", target.getUuid().toString())).first() != null) {
            banButton.setItem(XMaterial.RED_WOOL.parseItem());
            banButton.setName(coloredName + "&c's Bans");
            List<Document> countList = new ArrayList<>();
            allCases.filter(new Document("type", "ban").append("uuid", target.getUuid().toString())).into(countList);
            banButton.setAmount(countList.size());
            banButton.setAction(() -> {
                GUI.close(gui);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        CasesAlternative detailedPunishmentHistoryGUI = new CasesAlternative(player, 27, coloredName + "&2's Bans", plugin, target, PunishmentType.BAN);
                        detailedPunishmentHistoryGUI.setup(1);
                        GUI.open(detailedPunishmentHistoryGUI.getGui());
                    }
                }.runTaskLater(plugin, 1);
            });
        }
        if(allCases.filter(new Document("type", "blacklist").append("uuid", target.getUuid().toString())).first() != null) {
            blacklistButton.setItem(XMaterial.ORANGE_WOOL.parseItem());
            blacklistButton.setName(coloredName + "&c's Blacklists");
            List<Document> countList = new ArrayList<>();
            allCases.filter(new Document("type", "blacklist").append("uuid", target.getUuid().toString())).into(countList);
            blacklistButton.setAmount(countList.size());
            blacklistButton.setAction(() -> {
                GUI.close(gui);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        CasesAlternative detailedPunishmentHistoryGUI = new CasesAlternative(player, 27, coloredName + "&2's Blacklists", plugin, target, PunishmentType.BLACKLIST);
                        detailedPunishmentHistoryGUI.setup(1);
                        GUI.open(detailedPunishmentHistoryGUI.getGui());
                    }
                }.runTaskLater(plugin, 1);
            });
        }


        gui.setButton(1, kickButton);
        gui.setButton(3, muteButton);
        gui.setButton(5, banButton);
        gui.setButton(7, blacklistButton);
    }
}
