package io.github.mac_genius.scoreboardmaster;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * Created by Mac on 4/14/2015.
 * Sets the player's inventory on join.
 */
public class PlayerInventory {
    private Player player;


    public PlayerInventory(Player playerIn) {
        player = playerIn;
    }

    public void setPlayerInventory() {
        ArrayList<String> lore = new ArrayList<>();
        String loreString = ChatColor.GRAY + "" + ChatColor.ITALIC + "Brings up the different games available on the network!";
        lore.add(loreString);
        ItemStack compass = new ItemStack(Material.COMPASS);
        player.getInventory().clear();
        player.getInventory().setItem(0, compass);
        ItemStack compassScore = player.getInventory().getItem(0);
        ItemMeta compassData = compassScore.getItemMeta();
        compassData.setDisplayName(ChatColor.AQUA + "Server Selector  " + ChatColor.GRAY + "(Right Click to open)");
        compassData.setLore(lore);
        compassScore.setItemMeta(compassData);
    }

    public void setCompassInventory() {
        ArrayList<String> lore = new ArrayList<>();
        String loreString = ChatColor.GRAY + "" + ChatColor.ITALIC + "A world completely covered in Zombies!";
        lore.add(loreString);
        ItemStack zombieHead = new ItemStack(Material.SKULL, 1, (short) 2);
        ItemMeta compassData = zombieHead.getItemMeta();
        compassData.setDisplayName(ChatColor.GREEN + "Click to view online servers!");
        compassData.setLore(lore);
        zombieHead.setItemMeta(compassData);
    }
}
