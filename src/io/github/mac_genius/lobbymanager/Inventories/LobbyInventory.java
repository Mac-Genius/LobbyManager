package io.github.mac_genius.lobbymanager.Inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * Created by Mac on 4/14/2015.
 * Sets the player's inventory on join.
 */
public class LobbyInventory {
    private Player player;

    public LobbyInventory(Player playerIn) {
        player = playerIn;
        setPlayerInventory();
    }

    public void setPlayerInventory() {
        player.getInventory().clear();
        player.getInventory().setHeldItemSlot(0);
        player.getInventory().setItem(0, getServerIcon());
        if (player.hasPermission("lobbymanager.whitelist")) {
            player.getInventory().setItem(1, getWhitelistIcon());
        }
        player.getInventory().setItem(4, getStoreIcon());
        player.getInventory().setItem(8, getPreferenceIcon());
    }

    private ItemStack getServerIcon() {
        String name = ChatColor.AQUA + "Server Selector  " + ChatColor.GRAY + "(Right Click to open)";
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Brings up the different games available on the network!");
        return makeItem(Material.COMPASS, name, lore, (byte) 0);
    }

    private ItemStack getWhitelistIcon() {
        String name = ChatColor.GOLD + "Whitelist Manager  " + ChatColor.GRAY + "(Right Click to open)";
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "Allows you to edit the whitelist.");
        return makeItem(Material.BOOK, name, lore, (byte) 0);
    }

    private ItemStack getPreferenceIcon() {
        String name = ChatColor.RED + "Lobby Settings  " + ChatColor.GRAY + "(Right Click to open)";
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "Your lobby settings.");
        return makeItem(Material.PAPER, name, lore, (byte) 0);
    }

    private ItemStack getStoreIcon() {
        String name = ChatColor.GREEN + "One Stop Shop  " + ChatColor.GRAY + "(Right Click to open)";
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "A place to get all of your visual goods!");
        return makeItem(Material.EMERALD, name, lore, (byte) 0);
    }

    private ItemStack makeItem(Material type, String name, ArrayList<String> lore, byte color) {
        ItemStack item = new ItemStack(type, 1, color);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
}
