package io.github.mac_genius.lobbymanager.Inventories;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.mac_genius.lobbymanager.LobbyManager;
import io.github.mac_genius.lobbymanager.NPCHandler.NPCMessages;
import io.github.mac_genius.lobbymanager.ServerSettings;
import io.github.mac_genius.lobbymanager.database.ServerWhitelist;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
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

import java.util.ArrayList;

/**
 * Created by Mac on 1/20/2016.
 */
public class TestWorldSelect implements Listener {
    private ServerSettings settings;
    private Player player;
    private String serverType;
    private int cancelTask;

    public TestWorldSelect(ServerSettings settings, Player player, String serverType) {
        this.settings = settings;
        this.player = player;
        this.serverType = serverType;
        openInventory();
        cancelTask = LobbyManager.getSingleton().getServer().getScheduler().runTaskTimerAsynchronously(LobbyManager.getSingleton(), () -> {
            refreshInventory();
        }, 0, 20).getTaskId();
    }

    @EventHandler
    public void worldSelect(InventoryClickEvent event) {
        if (((Player) event.getWhoClicked()).equals(player)) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().getDisplayName() != null) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Survival")) {
                    ServerWhitelist whitelist = new ServerWhitelist(settings);
                    if (whitelist.getWhitelisted(event.getWhoClicked().getUniqueId().toString())) {
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("Connect");
                        out.writeUTF("Survival-1");
                        ((Player) event.getWhoClicked()).sendPluginMessage(settings.getPlugin(), "BungeeCord", out.toByteArray());
                        event.getWhoClicked().sendMessage("Sending you to Survival!");
                    } else {
                        NPCMessages messages = new NPCMessages(settings, (Player) event.getWhoClicked());
                        if (whitelist.getBanned(event.getWhoClicked().getUniqueId().toString())) {
                            for (String s : messages.getMessages("vanilla_register")) {
                                event.getWhoClicked().sendMessage(s);
                            }
                        } else if (whitelist.getWhitelistStatus(event.getWhoClicked().getUniqueId().toString()) == 0) {
                            for (String s : messages.getMessages("van_go")) {
                                event.getWhoClicked().sendMessage(s);
                            }
                        } else {
                            for (String s : messages.getMessages("van_wait")) {
                                event.getWhoClicked().sendMessage(s);
                            }
                        }
                    }
                } else {
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Connect");
                    out.writeUTF(event.getCurrentItem().getItemMeta().getDisplayName());
                    player.sendPluginMessage(LobbyManager.getSingleton(), "BungeeCord", out.toByteArray());
                }
                player.closeInventory();
                HandlerList.unregisterAll(this);
                LobbyManager.getSingleton().getServer().getScheduler().cancelTask(cancelTask);
            }
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
        ItemStack[] inventory = LobbyManager.getSingleton().getServerSelectManager().getServers(serverType, player);
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
        ItemStack[] inventory = LobbyManager.getSingleton().getServerSelectManager().getServers(serverType, player);
        player.getOpenInventory().getTopInventory().clear();
        for (int i = 0; i < inventory.length; i++) {
            player.getOpenInventory().getTopInventory().setItem(i, inventory[i]);
        }
    }
}
