package club.crestmc.neptunecarbonbukkit.punishmenthistory;

import club.crestmc.neptunecarbonbukkit.Constants;
import club.crestmc.neptunecarbonbukkit.commands.punishments.PunishmentType;
import club.crestmc.neptunecarbonbukkit.entities.UnknownPlayer;
import club.crestmc.neptunecarbonbukkit.gui.Button;
import club.crestmc.neptunecarbonbukkit.gui.GUI;
import club.crestmc.neptunecarbonbukkit.utils.ChatUtil;
import club.crestmc.neptunecarbonbukkit.utils.ColorUtil;
import club.crestmc.neptunecarbonbukkit.utils.ItemBuilder;
import club.crestmc.neptunecarbonbukkit.utils.Util;
import com.cryptomorin.xseries.XMaterial;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import static club.crestmc.neptunecarbonbukkit.gui.CustomGUI.gui;

public class JavaHistoryUtils {
    public Button createPunishmentButton(Document punishment, UnknownPlayer target, Player player) {
        ItemBuilder itemBuilder = new ItemBuilder(XMaterial.BEDROCK.parseItem(), 1, "&cAn error occured.", Arrays.asList("&cCould not fetch data."));

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

        String activePrefix = "&a[Active] ";
        String expiredPrefix = "&6[Expired] ";
        String revokedPrefix = "&c[Revoked] ";

        String coloredTarget = new ColorUtil().getColoredNameFromUuid(target.getUuid());

        PunishmentType type = PunishmentType.valueOf(punishment.getString("type").toUpperCase());

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

        return new Button(
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
    }
}
