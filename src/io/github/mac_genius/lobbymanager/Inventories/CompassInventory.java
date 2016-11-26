package io.github.mac_genius.lobbymanager.Inventories;

import io.github.mac_genius.lobbymanager.LobbyManager;
import io.github.mac_genius.lobbymanager.ServerSettings;
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

import java.util.*;

/**
 * Created by Mac on 6/22/2015.
 */
public class CompassInventory implements Listener {
    private ServerSettings settings;
    private Player player;
    private int updateCancel;

    public CompassInventory(ServerSettings settings, Player playerIn) {
        this.settings = settings;
        player = playerIn;
        openInventory();
        updateCancel = LobbyManager.getSingleton().getServer().getScheduler().runTaskTimerAsynchronously(LobbyManager.getSingleton(), () -> {
            updatePlayerCount();
        }, 0, 20).getTaskId();
    }

    public ArrayList<String> serverInfo(String serverNameIn, Player playerIn) {
        ArrayList<String> itemInfo = new ArrayList<>();
        if (serverNameIn.equalsIgnoreCase("survival")) {
            itemInfo.add(ChatColor.DARK_GRAY + "Genre: Vanilla");
            itemInfo.add(" ");
            itemInfo.add(ChatColor.WHITE + "This is a vanilla Minecraft");
            itemInfo.add(ChatColor.WHITE + "server running Minecraft 1.8.9.");
            itemInfo.add(ChatColor.WHITE + "Join for a plugin/mod free time!");
            itemInfo.add(" ");
            itemInfo.add(ChatColor.WHITE + playerAmount("Survival"));
        }
        if (serverNameIn.equalsIgnoreCase("test")) {
            itemInfo.add(ChatColor.DARK_GRAY + "Genre: Testing plugins");
            itemInfo.add(" ");
            itemInfo.add(ChatColor.WHITE + "Test new plugins and try");
            itemInfo.add(ChatColor.WHITE + "to break them!");
            itemInfo.add(" ");
            itemInfo.add(ChatColor.WHITE + playerAmount("TEST"));
        }
        return itemInfo;
    }

    public String playerAmount(String serverType) {
        int playerAmounts = LobbyManager.getSingleton().getServerSelectManager().getPlayerAmount(serverType);
        String amount = "There are currently ";

        if (playerAmounts < 0) {
            return "There are no available servers at this time.";
        }else {
            amount += playerAmounts + " player(s) playing this game!";
            return amount;
        }
    }

    public void updatePlayerCount() {
        if (player.hasPermission("lobbymanager.test")) {
            ItemStack survival = player.getOpenInventory().getTopInventory().getItem(12);
            ItemMeta meta = survival.getItemMeta();
            meta.setLore(serverInfo("Survival", player));
            survival.setItemMeta(meta);
            player.getOpenInventory().getTopInventory().setItem(12, survival);

            ItemStack test = player.getOpenInventory().getTopInventory().getItem(14);
            ItemMeta metaT = test.getItemMeta();
            metaT.setLore(serverInfo("TEST", player));
            survival.setItemMeta(metaT);
            player.getOpenInventory().getTopInventory().setItem(14, test);
        } else {
            ItemStack survival = player.getOpenInventory().getTopInventory().getItem(13);
            ItemMeta meta = survival.getItemMeta();
            meta.setLore(serverInfo("Survival", player));
            survival.setItemMeta(meta);
            player.getOpenInventory().getTopInventory().setItem(13, survival);
        }
    }

    private void openInventory() {
        Inventory serverTypes = Bukkit.createInventory(player, 27, "Server Menu:");
        if (player.hasPermission("lobbymanager.test")) {
            ItemStack survival = new ItemStack(Material.DIAMOND_PICKAXE, 1);
            ItemMeta itemInfo = survival.getItemMeta();
            itemInfo.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Survival");
            itemInfo.setLore(serverInfo("survival", player));
            survival.setItemMeta(itemInfo);
            serverTypes.setItem(12, survival);

            ItemStack test = new ItemStack(Material.REDSTONE_TORCH_ON, 1);
            ItemMeta itemMeta = test.getItemMeta();
            itemInfo.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Test Worlds");
            itemInfo.setLore(serverInfo("test", player));
            test.setItemMeta(itemInfo);
            serverTypes.setItem(14, test);
        } else {
            ItemStack survival = new ItemStack(Material.DIAMOND_PICKAXE, 1);
            ItemMeta itemInfo = survival.getItemMeta();
            itemInfo.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Survival");
            itemInfo.setLore(serverInfo("survival", player));
            survival.setItemMeta(itemInfo);
            serverTypes.setItem(13, survival);
        }
        player.openInventory(serverTypes);
    }

    @EventHandler
    public void closeInventory(InventoryCloseEvent event) {
        if (event.getPlayer().equals(player)) {
            LobbyManager.getSingleton().getServer().getScheduler().cancelTask(updateCancel);
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler
    public void selectItem(InventoryClickEvent event) {
        if (event.getWhoClicked().equals(player)) {
            LobbyManager.getSingleton().getServer().getScheduler().cancelTask(updateCancel);
            event.setCancelled(true);
            player.closeInventory();
            HandlerList.unregisterAll(this);
            int slot = event.getSlot();
            if (player.hasPermission("lobbymanager.test")) {
                switch (slot) {
                    case 12:
                        LobbyManager.getSingleton().getServer().getPluginManager().registerEvents(new TestWorldSelect(settings, player, "Survival"), LobbyManager.getSingleton());
                        break;
                    case 14:
                        LobbyManager.getSingleton().getServer().getPluginManager().registerEvents(new TestWorldSelect(settings, player, "TEST"), LobbyManager.getSingleton());
                        break;
                    default:
                        break;
                }
            } else {
                if (slot == 13) {
                    LobbyManager.getSingleton().getServer().getPluginManager().registerEvents(new TestWorldSelect(settings, player, "Survival"), LobbyManager.getSingleton());
                }
            }
        }
    }
}
