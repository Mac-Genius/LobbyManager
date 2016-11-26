package io.github.mac_genius.lobbymanager.Inventories;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * Created by Mac on 3/21/2016.
 */
public class AlterItem {
    public static ItemStack makeItem(Material type, String name, ArrayList<String> lore, byte color, int amount) {
        ItemStack item = new ItemStack(type, amount, color);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
}
