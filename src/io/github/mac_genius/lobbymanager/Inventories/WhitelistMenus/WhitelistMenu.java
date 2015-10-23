package io.github.mac_genius.lobbymanager.Inventories.WhitelistMenus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

import java.util.ArrayList;

/**
 * Created by Mac on 10/22/2015.
 */
public class WhitelistMenu implements Listener {
    private Plugin plugin;
    private Player player;

    public WhitelistMenu(Plugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        setInventory();
    }

    private void setInventory() {
        Inventory whitelistMenu = Bukkit.createInventory(player, 9, "Whitelist Menu");
        whitelistMenu.setItem(3, getWaitlist());
        whitelistMenu.setItem(4, getWhitelisted());
        whitelistMenu.setItem(5, getBanned());
        player.openInventory(whitelistMenu);
    }

    private ItemStack getWaitlist() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.ITALIC + "Displays the people who have registered.");
        String name = ChatColor.AQUA + "Waitlist";
        return makeItem(Material.MILK_BUCKET, name, lore);
    }

    private ItemStack getWhitelisted() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.ITALIC + "Displays the people who are whitelisted.");
        String name = ChatColor.AQUA + "Whitelisted";
        return makeItem(Material.MILK_BUCKET, name, lore);
    }

    private ItemStack getBanned() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.ITALIC + "Displays the people who have been banned.");
        String name = ChatColor.AQUA + "Banned";
        return makeItem(Material.MILK_BUCKET, name, lore);
    }

    private ItemStack makeItem(Material type, String name, ArrayList<String> lore) {
        ItemStack item = new ItemStack(type);
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
        }
    }

    @EventHandler
    public void click(InventoryClickEvent event) {
        if (event.getWhoClicked() == player) {
            if (event.getSlot() == 3) {
                HandlerList.unregisterAll(this);
                plugin.getServer().getPluginManager().registerEvents(new WaitlistMenu(plugin, player), plugin);
            } else if (event.getSlot() == 4) {
                HandlerList.unregisterAll(this);
                plugin.getServer().getPluginManager().registerEvents(new WhitelistedMenu(plugin, player), plugin);
            } else if (event.getSlot() == 5) {
                HandlerList.unregisterAll(this);
                plugin.getServer().getPluginManager().registerEvents(new BannedMenu(plugin, player), plugin);
            }
            event.setCancelled(true);
        }
    }
}
