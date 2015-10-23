package io.github.mac_genius.lobbymanager.Inventories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;

/**
 * Created by Mac on 6/22/2015.
 */
public class CompassInventory {
    private Plugin plugin;
    private String[] serverList;
    HashMap<String, String> playerCount;
    private Player player;

    public CompassInventory(Plugin pluginIn, Player playerIn, HashMap<String, String> playerCountIn) {
        plugin = pluginIn;
        player = playerIn;
        playerCount = playerCountIn;
    }

    public Inventory getInventory(Player playerIn) {
        Inventory serverTypes = Bukkit.createInventory(playerIn, 27, "Server Menu:");
        serverTypes.setItem(12, server("survival", playerIn));
        serverTypes.setItem(14, server("zombie", playerIn));
        return serverTypes;
    }

    public ArrayList<String> serverInfo(String serverNameIn, Player playerIn) {
        ArrayList<String> itemInfo = new ArrayList<>();
        if (serverNameIn.equalsIgnoreCase("survival")) {
            itemInfo.add(ChatColor.DARK_GRAY + "Genre: Vanilla");
            itemInfo.add(" ");
            itemInfo.add(ChatColor.WHITE + "This is a vanilla Minecraft");
            itemInfo.add(ChatColor.WHITE + "server running Minecraft 1.8.7.");
            itemInfo.add(ChatColor.WHITE + "Join for a plugin/mod free time!");
            itemInfo.add(" ");
            itemInfo.add(ChatColor.WHITE + playerAmount("Survival"));
        }
        if (serverNameIn.equalsIgnoreCase("zombie")) {
            itemInfo.add(ChatColor.DARK_GRAY + "Genre: Zombie Apocalypse");
            itemInfo.add(" ");
            itemInfo.add(ChatColor.WHITE + "Fight your way through hordes");
            itemInfo.add(ChatColor.WHITE + "of zombies while trying to");
            itemInfo.add(ChatColor.WHITE + "survive!");
            itemInfo.add(" ");
            itemInfo.add(ChatColor.WHITE + playerAmount("Zombie"));
        }
        return itemInfo;
    }

    public String playerAmount(String serverType) {
        String amount = "There are currently ";
        int playerAmounts = 0;
        if (serverType.contains(serverType)) {
            Set<String> stuff = playerCount.keySet();
            for (String key : stuff) {
                if (key.contains(serverType)) {
                    playerAmounts += Integer.parseInt(playerCount.get(key));
                }
            }
        }
        amount += playerAmounts + " player(s) on this server!";
        return amount;
    }

    public ItemStack server(String serverNameIn, Player playerIn) {
        ItemStack server;
        if (serverNameIn.equalsIgnoreCase("survival")) {
            server = new ItemStack(Material.DIAMOND_PICKAXE, 1);
            ItemMeta itemInfo = server.getItemMeta();
            itemInfo.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Survival");
            itemInfo.setLore(serverInfo("survival", playerIn));
            server.setItemMeta(itemInfo);
        }
        else if (serverNameIn.equalsIgnoreCase("zombie")) {
            server = new ItemStack(Material.ROTTEN_FLESH, 1);
            ItemMeta itemInfo = server.getItemMeta();
            itemInfo.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Zombie World");
            itemInfo.setLore(serverInfo("zombie", playerIn));
            server.setItemMeta(itemInfo);
        }
        else {
            server = new ItemStack(Material.STONE, 1);
        }
        return server;
    }

    public void updatePlayerCount() {
        for (ItemStack i : player.getOpenInventory().getTopInventory()) {
            if (i != null) {
                if (i.getItemMeta().getDisplayName().contains("Survival")) {
                    ItemMeta meta = i.getItemMeta();
                    meta.setLore(serverInfo("survival", player));
                    i.setItemMeta(meta);
                }
                if (i.getItemMeta().getDisplayName().contains("Zombie World")) {
                    ItemMeta meta = i.getItemMeta();
                    meta.setLore(serverInfo("zombie", player));
                    i.setItemMeta(meta);
                }
            }
        }
    }
}
