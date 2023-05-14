package club.crestmc.neptunecarbonbukkit.punishmenthistory.guis;

import club.crestmc.neptunecarbonbukkit.NeptuneCarbonBukkit;
import club.crestmc.neptunecarbonbukkit.commands.punishments.PunishmentType;
import club.crestmc.neptunecarbonbukkit.entities.UnknownPlayer;
import club.crestmc.neptunecarbonbukkit.gui.Button;
import club.crestmc.neptunecarbonbukkit.gui.CustomGUI;
import club.crestmc.neptunecarbonbukkit.gui.GUI;
import club.crestmc.neptunecarbonbukkit.gui.PagedGUI;
import club.crestmc.neptunecarbonbukkit.gui.guis.FindPageGUI;
import club.crestmc.neptunecarbonbukkit.punishmenthistory.HistoryUtils;
import club.crestmc.neptunecarbonbukkit.punishmenthistory.JavaHistoryUtils;
import club.crestmc.neptunecarbonbukkit.punishmenthistory.PunishmentsPage;
import club.crestmc.neptunecarbonbukkit.utils.ChatUtil;
import club.crestmc.neptunecarbonbukkit.utils.ItemBuilder;
import club.crestmc.neptunecarbonbukkit.utils.Util;
import com.cryptomorin.xseries.XMaterial;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CasesAlternative extends CustomGUI {
    private NeptuneCarbonBukkit plugin;
    private AtomicInteger newPage;
    private HashSet<PunishmentsPage> pages;
    private FindIterable<Document> allCases;
    private UnknownPlayer target;
    private PunishmentType type;
    public CasesAlternative(Player player, int size, String title, NeptuneCarbonBukkit plugin, UnknownPlayer target, PunishmentType type) {
        super(player, size, title);
        this.plugin = plugin;
        newPage = new AtomicInteger(1);

        this.allCases = plugin.getDatabaseManager().punishmentsCollection.find(new Document().append("uuid", target.getUuid().toString()));
        this.pages = HistoryUtils.INSTANCE.createPages(allCases, type);
        this.target = target;
        this.type = type;
    }

    public void setup(int pageThing) {

        player.sendMessage(ChatUtil.INSTANCE.translate("&aFetching all " + type.toString().toLowerCase() + "s for " + target.getUsername() + ", this could take some time."));

        int page = pageThing;

        gui.setFiller(new int[] { 0,1,2,3,4,5,6,7,8 });

        gui.setFiller(new int[] { 18,19,20,21,22,23,24,25,26 });

        setPage(newPage);
    }

    public void setPage(AtomicInteger pageNumber) {
        player.sendMessage(ChatUtil.INSTANCE.translate("&aFetching page " + pageNumber.get() + " for " + target.getUsername() + ", this could take some time."));
        PunishmentsPage[] pagesArray = getPagesArray();

        ItemBuilder previousPage = new ItemBuilder(XMaterial.GRAY_DYE.parseItem(), 1, ChatUtil.INSTANCE.translate("&cYou are on the first page."), Collections.emptyList());

        Runnable setPageRunnable = () -> {
            if(newPage.get() <= 1) {
                player.sendMessage(ChatUtil.INSTANCE.translate("&cError: You are on the first page."));
            } else {
                newPage.set(newPage.get() - 1);
                setPage(newPage);
            }
        };

        if(!(newPage.get() <= 1)) {
            previousPage.setName(ChatUtil.INSTANCE.translate("&aPrevious Page"));
            previousPage.setItem(XMaterial.LIME_DYE.parseItem());
        }

        gui.setButton(0, new Button(previousPage.getItem(), setPageRunnable, previousPage.getName()));

        // NEXT PAGE
        ItemBuilder nextPage = new ItemBuilder(XMaterial.GRAY_DYE.parseItem(), 1, ChatUtil.INSTANCE.translate("&cYou are on the last page."), Collections.emptyList());

        Runnable nextPageRunnable = () -> {
            if(newPage.get() + 1 >= getPagesArray().length + 1) {
                player.sendMessage(ChatUtil.INSTANCE.translate("&cError: You are on the last page."));
            } else {
                newPage.set(newPage.get() + 1);
                setPage(newPage);
            }
        };

        if(!(newPage.get() + 1 >= getPagesArray().length + 1)) {
            nextPage.setName(ChatUtil.INSTANCE.translate("&aNext Page"));
            nextPage.setItem(XMaterial.LIME_DYE.parseItem());
        }

        gui.setButton(8, new Button(nextPage.getItem(), nextPageRunnable, nextPage.getName()));

        System.out.println("PAGES" + pages);
        System.out.println("PAGES ARRAY" + pagesArray);
        System.out.println(pageNumber);

        PunishmentsPage page = pagesArray[pageNumber.get() - 1];

        int loop = 9;

        gui.setFiller(new int[] { 9,10,11,12,13,14,15,16,17 });

        for (Document doc : page.getPunishments()) {
            gui.setButton(loop, new JavaHistoryUtils().createPunishmentButton(doc, target, player));
            loop++;
        }
    }
    public PunishmentsPage[] getPagesArray() {
        ArrayList<PunishmentsPage> pagesArray = new ArrayList<>();
        for (PunishmentsPage page : pages) {
            pagesArray.add(page);
            System.out.println("ADDING FOLLOWING PAGE:");
            System.out.println(page);
        }

        return pagesArray.toArray(new PunishmentsPage[0]);
    }
}
