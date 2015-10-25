package io.github.mac_genius.lobbymanager.Inventories.StoreMenus;

import io.github.mac_genius.lobbymanager.ServerSettings;
import io.github.mac_genius.lobbymanager.database.SQLObjects.Pet;
import io.github.mac_genius.lobbymanager.database.SQLObjects.PetMenuObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Mac on 10/23/2015.
 */
public class StoreMenu implements Listener {
    private ServerSettings settings;
    private Player player;
    private int fancyshopID;

    public StoreMenu(ServerSettings settings, Player player) {
        this.settings = settings;
        this.player = player;
        setInventory();
    }

    private void setInventory() {
        Inventory store = Bukkit.createInventory(player, 27, "One Stop Shop");
        store.setContents(setRainbowBorder());
        store.setItem(12, getGadgetIcon());
        store.setItem(14, getPetIcon());
        player.openInventory(store);
        //BukkitTask  fancyShop = settings.getPlugin().getServer().getScheduler().runTaskTimer(settings.getPlugin(), new FancyShop(), 0, 2);
        //fancyshopID = fancyShop.getTaskId();
    }

    private ItemStack[] setBorder() {
        ItemStack[] border = new ItemStack[27];
        for (int i = 0; i < 27; i++) {
            if (i < 9) {
                border[i] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.GREEN + "", new ArrayList<>(), (byte) 15);
            } else if ((i + 1) % 9 == 0 || i % 9 == 0) {
                border[i] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.GREEN + "", new ArrayList<>(), (byte) 15);
            } else if (i > 17 && i < 27) {
                border[i] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.GREEN + "", new ArrayList<>(), (byte) 15);
            }
        }
        return border;
    }

    private ItemStack[] setRainbowBorder() {
        ItemStack[] border = new ItemStack[27];
        border[0] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.BLACK + "", new ArrayList<>(), (byte) 14);
        border[1] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.BLACK + "", new ArrayList<>(), (byte) 1);
        border[2] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.BLACK + "", new ArrayList<>(), (byte) 4);
        border[3] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.BLACK + "", new ArrayList<>(), (byte) 5);
        border[4] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.BLACK + "", new ArrayList<>(), (byte) 3);
        border[5] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.BLACK + "", new ArrayList<>(), (byte) 2);
        border[6] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.BLACK + "", new ArrayList<>(), (byte) 6);
        border[7] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.BLACK + "", new ArrayList<>(), (byte) 14);
        border[8] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.BLACK + "", new ArrayList<>(), (byte) 1);
        border[9] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.BLACK + "", new ArrayList<>(), (byte) 1);
        border[17] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.BLACK + "", new ArrayList<>(), (byte) 4);
        border[18] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.BLACK + "", new ArrayList<>(), (byte) 5);
        border[19] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.BLACK + "", new ArrayList<>(), (byte) 3);
        border[20] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.BLACK + "", new ArrayList<>(), (byte) 2);
        border[21] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.BLACK + "", new ArrayList<>(), (byte) 6);
        border[22] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.BLACK + "", new ArrayList<>(), (byte) 14);
        border[23] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.BLACK + "", new ArrayList<>(), (byte) 1);
        border[24] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.BLACK + "", new ArrayList<>(), (byte) 4);
        border[25] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.BLACK + "", new ArrayList<>(), (byte) 5);
        border[26] = makeItem(Material.STAINED_GLASS_PANE, ChatColor.BLACK + "", new ArrayList<>(), (byte) 3);
        return border;
    }

    private ItemStack getGadgetIcon() {
        String name = ChatColor.GREEN + "Gadgets";
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Gadgets that make you say 'what?'.");
        return makeItem(Material.PAPER, name, lore, (byte) 0);
    }

    private ItemStack getPetIcon() {
        String name = ChatColor.GREEN + "Pet Shop";
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Get your pets here!");
        return makeItem(Material.PAPER, name, lore, (byte) 0);
    }

    private ItemStack makeItem(Material type, String name, ArrayList<String> lore, byte color) {
        ItemStack item = new ItemStack(type, 1, color);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void close(InventoryCloseEvent event) {
        if (event.getPlayer() == player) {
            HandlerList.unregisterAll(this);
            settings.getPlugin().getServer().getScheduler().cancelTask(fancyshopID);
        }
    }

    @EventHandler
    public void click(InventoryClickEvent event) {
        if (event.getWhoClicked() == player) {
            if (event.getSlot() == 14) {
                HandlerList.unregisterAll(this);
                settings.getPlugin().getServer().getScheduler().cancelTask(fancyshopID);
                settings.getPlugin().getServer().getPluginManager().registerEvents(new PetMenu(settings, player), settings.getPlugin());
            }
            event.setCancelled(true);
        }
    }

    public class FancyShop implements Runnable {
        @Override
        public void run() {
            updateRainbowBorder();
        }
    }

    private int generateRandomSlot() {
        int slot = new Random().nextInt(27);
        if (slot == 12) {
            slot = generateRandomSlot();
        }
        return slot;
    }

    private void updateRainbowBorder() {

    }
}
