package io.github.mac_genius.lobbymanager.Inventories.StoreMenus;

import io.github.mac_genius.lobbymanager.ServerSettings;
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
public class BoneMenu implements Listener {
    private ServerSettings settings;
    private Player player;
    private PetMenuObject petStore;
    private int fancyshopID;

    public BoneMenu(ServerSettings settings, Player player) {
        this.settings = settings;
        this.player = player;
        petStore = new io.github.mac_genius.lobbymanager.database.PetMenu(settings).getMenu(player.getUniqueId().toString());
        setInventory();
        BukkitTask fancyShop = settings.getPlugin().getServer().getScheduler().runTaskTimer(settings.getPlugin(), new FancyShop(), 0, 2);
        fancyshopID = fancyShop.getTaskId();
    }

    private void setInventory() {
        Inventory store = Bukkit.createInventory(player, 27, "Pet Info");
        store.setContents(setRainbowBorder());
        store.setItem(12, getPetIcon());
        store.setItem(14, rename());
        player.openInventory(store);
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

    private ItemStack getPetIcon() {
        String name = ChatColor.LIGHT_PURPLE + "De-equip pet";
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "Left-click to remove pet.");
        return makeItem(Material.REDSTONE, name, lore, (byte) 0);
    }

    private ItemStack rename() {
        String name = ChatColor.LIGHT_PURPLE + "Rename pet";
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "Left-click to rename pet.");
        return makeItem(Material.ANVIL, name, lore, (byte) 0);
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
            if (event.getSlot() == 12) {
                HandlerList.unregisterAll(this);
                settings.getPlayerPets().get(player).remove();
                settings.getPlayerPets().remove(player);
                player.getInventory().setItem(5, new ItemStack(Material.AIR));
                settings.getPlugin().getServer().getScheduler().cancelTask(fancyshopID);
                player.closeInventory();
            } if (event.getSlot() == 14) {
                HandlerList.unregisterAll(this);
                settings.getPlugin().getServer().getPluginManager().registerEvents(new Rename(settings, player), settings.getPlugin());
                settings.getPlugin().getServer().getScheduler().cancelTask(fancyshopID);
                player.closeInventory();
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
