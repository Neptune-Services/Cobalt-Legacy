package club.crestmc.neptunecarbonbukkit.gui.guis;

import club.crestmc.neptunecarbonbukkit.Constants;
import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit;
import club.crestmc.neptunecarbonbukkit.commands.punishments.PunishmentType;
import club.crestmc.neptunecarbonbukkit.entities.UnknownPlayer;
import club.crestmc.neptunecarbonbukkit.gui.*;
import club.crestmc.neptunecarbonbukkit.utils.ChatUtil;
import club.crestmc.neptunecarbonbukkit.utils.ColorUtil;
import club.crestmc.neptunecarbonbukkit.utils.ItemBuilder;
import club.crestmc.neptunecarbonbukkit.utils.Util;
import com.cryptomorin.xseries.XMaterial;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class CasesDetailed extends CustomGUI implements PagedGUI {
    private final NeptuneCarbonBukkit plugin;
    public CasesDetailed(Player player, int size, String title) {
        super(player, size, title);
        plugin = NeptuneCarbonBukkit.getPlugin(NeptuneCarbonBukkit.class);
    }

    @Override
    public void setupPagedGUI(Map<Integer, Button> buttons, int page) {
        for (Map.Entry<Integer, Button> entry : buttons.entrySet()) {
            int[] data = Util.Companion.getHistorySlotData(entry.getKey());
            if (page == data[0])
                gui.setButton(data[1] + 9, entry.getValue());
        }
    }

    public void setup(PunishmentType type, Player player, UnknownPlayer target, int page) {

        player.sendMessage(ChatUtil.INSTANCE.translate("&aGathering all " + type.toString().toLowerCase() + "s for " + target.getUsername() + ", this may take some time..."));

        String coloredTarget = new ColorUtil().getColoredNameFromUuid(target.getUuid());

        String activePrefix = "&a&l(Active) ";
        String revokedPrefix = "&4&l(Revoked) ";
        String expiredPrefix = "&6&l(Expired) ";

        Map<Integer, Button> buttons = new HashMap<>();
        Set<Integer> pages = new HashSet<>();

        FindIterable<Document> cases = plugin.getDatabaseManager().punishmentsCollection.find(new Document().append("uuid", target.getUuid().toString()));

        System.out.println(cases.first());

        MongoCursor<Document> cursor = cases.iterator();

        if (cases.filter(new Document("type", type.toString().toLowerCase())).first() != null) {
            int loop = -1;
            while (cursor.hasNext()) {
                Document punishment = cursor.next();
                System.out.println(punishment);
                if (Objects.equals(punishment.getString("type"), type.toString().toLowerCase())) {
                    loop++;

                    String executor;
                    if (punishment.getString("moderatorUuid").equalsIgnoreCase("CONSOLE")) executor = "&c&lConsole";
                    else executor = new ColorUtil().getColoredNameFromUuid(UUID.fromString(punishment.getString("moderatorUuid")));

                    String duration;
                    if (punishment.getDate("expires") == null)
                        duration = "Forever";
                    else {
                        Instant now = Instant.now();
                        Instant futureInstant = punishment.getDate("expires").toInstant();
                        Duration durationObj = Duration.between(now, futureInstant);

                        duration = Util.Companion.getExpirationDate(durationObj.toMillis());
                    }
                    String reason = punishment.getString("reason");

                    ItemBuilder itemBuilder = new ItemBuilder(XMaterial.BEDROCK.parseItem(), 1, "&3Punishment #" + punishment.getString("punishmentId"), ItemBuilder.Companion.formatLore(new String[]{
                            "&7&m----------------------------",
                            "&7Punishment details not yet loaded.",
                            "&7Please contact a developer if you see this.",
                            "&7&m----------------------------"
                    }));

                    switch (punishment.getString("status")) {
                        case "active":
                            itemBuilder.setItem(XMaterial.LIME_WOOL.parseItem());
                            itemBuilder.setName(activePrefix + Util.Companion.getExpirationDate(punishment.getDate("date").getTime()));

                            itemBuilder.setLore(ItemBuilder.Companion.formatLore(new String[]{
                                    Constants.INSTANCE.getPrimaryColor() + "&m----------------------------",
                                    Constants.INSTANCE.getPrimaryColor() + "Target: " + Constants.INSTANCE.getSecondaryColor() + coloredTarget,
                                    Constants.INSTANCE.getPrimaryColor() + "Duration: &d" + duration,
                                    Constants.INSTANCE.getPrimaryColor() + "&m----------------------------",
                                    Constants.INSTANCE.getPrimaryColor() + "Issued By: " + Constants.INSTANCE.getSecondaryColor() + executor,
                                    Constants.INSTANCE.getPrimaryColor() + "Issued Reason: " + Constants.INSTANCE.getSecondaryColor() + reason,
                                    Constants.INSTANCE.getPrimaryColor() + "Issued On: " + Constants.INSTANCE.getSecondaryColor() + ((punishment.getString("server") != null) ? punishment.getString("server") : "Unknown"),
                                    Constants.INSTANCE.getPrimaryColor() + "Expires: " + Constants.INSTANCE.getSecondaryColor() + ((punishment.getDate("expires") != null) ? Util.Companion.getExpirationDate(punishment.getDate("expires").getTime()) : "Never"),
                                    Constants.INSTANCE.getPrimaryColor() + "&m----------------------------"
                            }));

                            if (punishment.getString("status").equalsIgnoreCase("Active")
                                    && player.hasPermission("neptunecarbon.un" + type.toString().toLowerCase())
                                    && type != PunishmentType.KICK)
                                itemBuilder.getLore().add(ChatUtil.INSTANCE.translate("&aClick to revoke this punishment."));
                            break;
                        case "revoked":
                            itemBuilder.setItem(XMaterial.RED_WOOL.parseItem());
                            itemBuilder.setName(revokedPrefix + Util.Companion.getExpirationDate(punishment.getDate("date").getTime()));

                            String executorRemoved;
                            if (punishment.getString("revokedBy").equalsIgnoreCase("CONSOLE")) executorRemoved = "&c&lConsole";
                            else executorRemoved = new ColorUtil().getColoredNameFromUuid(UUID.fromString(punishment.getString("revokedBy")));

                            itemBuilder.setLore(ItemBuilder.Companion.formatLore(new String[]{
                                    Constants.INSTANCE.getPrimaryColor() + "&m----------------------------",
                                    Constants.INSTANCE.getPrimaryColor() + "Target: " + Constants.INSTANCE.getSecondaryColor() + coloredTarget,
                                    Constants.INSTANCE.getPrimaryColor() + "Duration: &d" + duration,
                                    Constants.INSTANCE.getPrimaryColor() + "&m----------------------------",
                                    Constants.INSTANCE.getPrimaryColor() + "Issued By: " + Constants.INSTANCE.getSecondaryColor() + executor,
                                    Constants.INSTANCE.getPrimaryColor() + "Issued Reason: " + Constants.INSTANCE.getSecondaryColor() + reason,
                                    Constants.INSTANCE.getPrimaryColor() + "Issued On: " + Constants.INSTANCE.getSecondaryColor() + ((punishment.getString("server") != null) ? punishment.getString("server") : "Unknown"),
                                    Constants.INSTANCE.getPrimaryColor() + "Expires: " + Constants.INSTANCE.getSecondaryColor() + ((punishment.getDate("expires") != null) ? Util.Companion.getExpirationDate(punishment.getDate("expires").getTime()) : "Never"),
                                    Constants.INSTANCE.getPrimaryColor() + "&m----------------------------",
                                    Constants.INSTANCE.getPrimaryColor() + "Revoked By: " + Constants.INSTANCE.getSecondaryColor() + executorRemoved,
                                    Constants.INSTANCE.getPrimaryColor() + "Revoked Reason: " + Constants.INSTANCE.getSecondaryColor() + punishment.getString("revokedReason"),
                                    Constants.INSTANCE.getPrimaryColor() + "Revoked Time: " + Constants.INSTANCE.getSecondaryColor() + Util.Companion.getExpirationDate(punishment.getDate("revokedDate").getTime()),
                                    Constants.INSTANCE.getPrimaryColor() + "Revoked On: " + Constants.INSTANCE.getSecondaryColor() + ((punishment.getString("revokedServer") != null) ? punishment.getString("server") : "Unknown"),
                                    Constants.INSTANCE.getPrimaryColor() + "&m----------------------------"
                            }));

                            break;
                        case "expired":
                            itemBuilder.setItem(XMaterial.ORANGE_WOOL.parseItem());
                            itemBuilder.setName(expiredPrefix + Util.Companion.getExpirationDate(punishment.getDate("date").getTime()));

                            itemBuilder.setLore(ItemBuilder.Companion.formatLore(new String[]{
                                    Constants.INSTANCE.getPrimaryColor() + "&m----------------------------",
                                    Constants.INSTANCE.getPrimaryColor() + "Target: " + Constants.INSTANCE.getSecondaryColor() + coloredTarget,
                                    Constants.INSTANCE.getPrimaryColor() + "Duration: &d" + duration,
                                    Constants.INSTANCE.getPrimaryColor() + "&m----------------------------",
                                    Constants.INSTANCE.getPrimaryColor() + "Issued By: " + Constants.INSTANCE.getSecondaryColor() + executor,
                                    Constants.INSTANCE.getPrimaryColor() + "Issued Reason: " + Constants.INSTANCE.getSecondaryColor() + reason,
                                    Constants.INSTANCE.getPrimaryColor() + "Issued On: " + Constants.INSTANCE.getSecondaryColor() + ((punishment.getString("server") != null) ? punishment.getString("server") : "Unknown"),
                                    Constants.INSTANCE.getPrimaryColor() + "Expired On: " + Constants.INSTANCE.getSecondaryColor() + ((punishment.getDate("expires") != null) ? Util.Companion.getExpirationDate(punishment.getDate("expires").getTime()) : "Never"),
                                    Constants.INSTANCE.getPrimaryColor() + "&m----------------------------",
                                    Constants.INSTANCE.getPrimaryColor() + "Expired Time: " + Constants.INSTANCE.getSecondaryColor() + Util.Companion.getExpirationDate(punishment.getDate("expiredDate").getTime()),
                                    Constants.INSTANCE.getPrimaryColor() + "&m----------------------------"
                            }));


                            break;
                    }

                    Button button = new Button(
                            itemBuilder.getItem(),
                            () -> {
                                if (type != PunishmentType.KICK && player.hasPermission("neptunecarbon.un" + type.toString().toLowerCase())
                                        && itemBuilder.getLore().contains(ChatUtil.INSTANCE.translate("&aClick to revoke this punishment."))) {
                                    GUI.close(gui);
                                    player.performCommand("un" + type.toString().toLowerCase() + " " + target.getUsername() + " -s Pardoned (Cases Menu)");
                                }
                            },
                            itemBuilder.getName(),
                            itemBuilder.getLore()
                    );

                    buttons.put(loop, button);
                }
            }

            for (Map.Entry<Integer, Button> entry : buttons.entrySet()) pages.add((entry.getKey() / 9) + 1);

            Toolbar toolbar = new Toolbar(getGui(), "Punishments", page, new ArrayList<>(pages), () -> new BukkitRunnable() {
                @Override
                public void run() {
                    CasesDetailed detailedPunishmentHistoryGUI = new CasesDetailed(player, 27, coloredTarget + "&2's " + type.toString().toLowerCase() + "&2s");
                    detailedPunishmentHistoryGUI.setup(type, player, target, Toolbar.getNewPage().get());
                    GUI.open(CasesDetailed.getGui());
                }
            }.runTaskLater(plugin, 1));

            toolbar.create(target, type, true);
            setupPagedGUI(buttons, page);
        }
    }
}
