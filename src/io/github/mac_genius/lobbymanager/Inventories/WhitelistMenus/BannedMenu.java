package io.github.mac_genius.lobbymanager.Inventories.WhitelistMenus;

import io.github.mac_genius.lobbymanager.database.ServerWhitelist;
import io.github.mac_genius.lobbymanager.database.SQLObjects.WhitelistResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
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
public class BannedMenu implements Listener {
    private Plugin plugin;
    private Player player;
    private ServerWhitelist whitelist;
    private int inventorySize;

    public BannedMenu(Plugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        whitelist = new ServerWhitelist(plugin);
        setInventory();
    }

    private void setInventory() {
        int size = whitelist.waitlistSize();
        if (size < 10) {
            size = 18;
        } else if (size < 19) {
            size = 27;
        } else if (size < 28) {
            size = 36;
        } else if (size < 37) {
            size = 45;
        } else {
            size = 54;
        }
        inventorySize = size;
        Inventory bannedMenu = Bukkit.createInventory(player, size, "Banned List Menu");
        bannedMenu.setContents(itemStacks(size));
        bannedMenu.setItem(size - 1, getBackButton());
        player.openInventory(bannedMenu);
    }

    private ItemStack[] itemStacks(int size) {
        ItemStack[] list = new ItemStack[size];
        ArrayList<WhitelistResult> banList = whitelist.getBanList();
        int i = 0;
        for (WhitelistResult r : banList) {
            String name = r.getName();
            ArrayList<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(r.getUuid());
            lore.add("");
            lore.add("Left click to add to the whitelist.");
            list[i] = makeItem(Material.ARMOR_STAND, name, lore, (byte) 0);
            i++;
        }

        return list;
    }

    private ItemStack getBackButton() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.ITALIC + "Goes back to the main menu.");
        String name = ChatColor.AQUA + "Back";
        return makeItem(Material.STAINED_GLASS_PANE, name, lore, (byte) 14);
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
        }
    }

    @EventHandler
    public void click(InventoryClickEvent event) {
        if (event.getWhoClicked() == player) {
            if (event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE && event.getSlot() == inventorySize - 1) {
                HandlerList.unregisterAll(this);
                plugin.getServer().getPluginManager().registerEvents(new WhitelistMenu(plugin, player), plugin);
            } else {
                if (event.getClick() == ClickType.LEFT) {
                    whitelist.setWhitelistStatus(event.getCurrentItem().getItemMeta().getLore().get(1), 2);
                    HandlerList.unregisterAll(this);
                    plugin.getServer().getPluginManager().registerEvents(new BannedMenu(plugin, player), plugin);
                    player.sendMessage(ChatColor.GREEN + "Player has been added to the whitelist.");
                }
            }
            event.setCancelled(true);
        }
    }
}
