package io.github.mac_genius.lobbymanager.SecondaryThreads;

import io.github.mac_genius.lobbymanager.ServerSettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * Created by Mac on 10/25/2015.
 */
public class UpdateShop implements Runnable {
    private ServerSettings settings;

    public UpdateShop(ServerSettings settings) {
        this.settings = settings;
    }

    @Override
    public void run() {
        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (Player player : players) {
            if (player.getOpenInventory().getTopInventory() != null) {
                for (int i = 0; i < player.getOpenInventory().getTopInventory().getSize(); i++) {
                    try {
                        if (i != 12 && i != 14) {
                            if (player.getOpenInventory().getTopInventory().getItem(i) != null) {
                                if (player.getOpenInventory().getTopInventory().getItem(i).getDurability() == (byte) 14) {
                                    player.getOpenInventory().getTopInventory().setItem(i, makeItem(Material.STAINED_GLASS_PANE, ChatColor.GREEN + "", new ArrayList<>(), (byte) 6));
                                } else if (player.getOpenInventory().getTopInventory().getItem(i).getDurability() == (byte) 1) {
                                    player.getOpenInventory().getTopInventory().setItem(i, makeItem(Material.STAINED_GLASS_PANE, ChatColor.GREEN + "", new ArrayList<>(), (byte) 14));
                                } else if (player.getOpenInventory().getTopInventory().getItem(i).getDurability() == (byte) 4) {
                                    player.getOpenInventory().getTopInventory().setItem(i, makeItem(Material.STAINED_GLASS_PANE, ChatColor.GREEN + "", new ArrayList<>(), (byte) 1));
                                } else if (player.getOpenInventory().getTopInventory().getItem(i).getDurability() == (byte) 5) {
                                    player.getOpenInventory().getTopInventory().setItem(i, makeItem(Material.STAINED_GLASS_PANE, ChatColor.GREEN + "", new ArrayList<>(), (byte) 4));
                                } else if (player.getOpenInventory().getTopInventory().getItem(i).getDurability() == (byte) 3) {
                                    player.getOpenInventory().getTopInventory().setItem(i, makeItem(Material.STAINED_GLASS_PANE, ChatColor.GREEN + "", new ArrayList<>(), (byte) 5));
                                } else if (player.getOpenInventory().getTopInventory().getItem(i).getDurability() == (byte) 2) {
                                    player.getOpenInventory().getTopInventory().setItem(i, makeItem(Material.STAINED_GLASS_PANE, ChatColor.GREEN + "", new ArrayList<>(), (byte) 3));
                                } else if (player.getOpenInventory().getTopInventory().getItem(i).getDurability() == (byte) 6) {
                                    player.getOpenInventory().getTopInventory().setItem(i, makeItem(Material.STAINED_GLASS_PANE, ChatColor.GREEN + "", new ArrayList<>(), (byte) 2));
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                    }
                }
            }
        }
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
