package io.github.mac_genius.lobbymanager.Inventories;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.mac_genius.lobbymanager.LobbyManager;
import io.github.mac_genius.lobbymanager.NPCHandler.NPCMessages;
import io.github.mac_genius.lobbymanager.database.ServerWhitelist;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Mac on 3/22/2016.
 */
public class LobbySelect implements Listener {
    private Player player;
    private int cancelTask;

    public LobbySelect(Player player) {
        this.player = player;
        openInventory();
        cancelTask = LobbyManager.getSingleton().getServer().getScheduler().runTaskTimerAsynchronously(LobbyManager.getSingleton(), () -> {
            refreshInventory();
        }, 0, 20).getTaskId();
    }

    @EventHandler
    public void worldSelect(InventoryClickEvent event) {
        if (((Player) event.getWhoClicked()).equals(player)) {
            event.setCancelled(true);
            player.closeInventory();
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(event.getCurrentItem().getItemMeta().getDisplayName());
            player.sendPluginMessage(LobbyManager.getSingleton(), "BungeeCord", out.toByteArray());

            HandlerList.unregisterAll(this);
            LobbyManager.getSingleton().getServer().getScheduler().cancelTask(cancelTask);
        }
    }

    @EventHandler
    public void close(InventoryCloseEvent event) {
        if (event.getPlayer().equals(player)) {
            HandlerList.unregisterAll(this);
            LobbyManager.getSingleton().getServer().getScheduler().cancelTask(cancelTask);
        }
    }

    public void openInventory() {
        ItemStack[] inventory = LobbyManager.getSingleton().getServerSelectManager().getLobbyServers(player);
        Inventory open;
        if (inventory.length <= 9) {
            open = Bukkit.createInventory(player, 9, "Server Select");
        } else if (inventory.length > 9 && inventory.length <= 18) {
            open = Bukkit.createInventory(player, 18, "Server Select");
        } else if (inventory.length > 18 && inventory.length <= 27) {
            open = Bukkit.createInventory(player, 27, "Server Select");
        } else if (inventory.length > 27 && inventory.length <= 36) {
            open = Bukkit.createInventory(player, 36, "Server Select");
        } else if (inventory.length > 36 && inventory.length <= 45) {
            open = Bukkit.createInventory(player, 45, "Server Select");
        } else {
            open = Bukkit.createInventory(player, 54, "Server Select");
        }
        for (int i = 0; i < inventory.length; i++) {
            open.setItem(i, inventory[i]);
        }
        player.openInventory(open);
    }

    private void refreshInventory() {
        ItemStack[] inventory = LobbyManager.getSingleton().getServerSelectManager().getLobbyServers(player);
        player.getOpenInventory().getTopInventory().clear();
        for (int i = 0; i < inventory.length; i++) {
            player.getOpenInventory().getTopInventory().setItem(i, inventory[i]);
        }
    }
}
