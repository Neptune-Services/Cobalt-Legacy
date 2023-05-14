package club.crestmc.neptunecarbonbukkit.utils;

import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit;
import javafx.util.Pair;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Comparator;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.stream.Collectors;

public class RankUtil {

    private static final NeptuneCarbonBukkit plugin = JavaPlugin.getPlugin(NeptuneCarbonBukkit.class);

    public static int getPriority(UUID uuid) {
        int priority = 0;

        LuckPerms luckPerms = LuckPermsProvider.get();
        GroupManager groupManager = luckPerms.getGroupManager();
        UserManager userManager = luckPerms.getUserManager();
        Group primaryGroup = groupManager.getGroup(userManager.getUser(uuid).getPrimaryGroup());
        priority = primaryGroup.getWeight().getAsInt();

        return priority;
    }

    public static int getRankPriority(String rank) {
        int priority = 0;

        LuckPerms luckPerms = LuckPermsProvider.get();
        GroupManager groupManager = luckPerms.getGroupManager();
        Group primaryGroup = groupManager.getGroup(rank);
        priority = primaryGroup.getWeight().getAsInt();

        return priority;
    }

    public static String getOrderedRanks() {
        String ranks = "No Ranks Found";

        LuckPerms luckPerms = LuckPermsProvider.get();
        GroupManager groupManager = luckPerms.getGroupManager();

        ranks = groupManager.getLoadedGroups()
                .stream()
                .filter(group -> group.getCachedData().getMetaData().getMetaValue("hidden") == null)
                .sorted(Comparator.comparingInt(group -> {
                    OptionalInt weight = group.getWeight();
                    return weight.isPresent() ? -weight.getAsInt() : 0;
                }))
                .map(Group::getDisplayName)
                .collect(Collectors.joining(ChatColor.WHITE + ", " + ChatColor.RESET, ChatColor.GRAY + "", ChatColor.RESET + ""));

        return ranks;
    }

}
