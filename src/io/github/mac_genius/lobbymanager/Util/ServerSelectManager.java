package io.github.mac_genius.lobbymanager.Util;

import io.github.mac_genius.lobbymanager.Inventories.AlterItem;
import io.github.mac_genius.lobbymanager.LobbyManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by Mac on 3/21/2016.
 */
public class ServerSelectManager {
    TreeSet<ServerInfo> serverList;

    public ServerSelectManager() {
        serverList = new TreeSet<>();
    }

    public int getPlayerAmount(String serverType) {
        int playersTotal = 0;
        boolean found = false;
        for (ServerInfo i : serverList) {
            if (i.getName().startsWith(serverType)) {
                if (i.isOnline()) {
                    playersTotal += i.getPlayers();
                    found = true;
                }
            }
        }
        return found ? playersTotal : -1;
    }

    public ItemStack[] getServers(String serverType, Player player) {
        ArrayList<ItemStack> servers = new ArrayList<>();
        int index = 1;
        for (ServerInfo i : serverList) {
            if (index <= 54) {
                if (i.getName().startsWith(serverType)) {
                    if (i.isOnline() && i.isCanConnect()) {
                        ArrayList<String> lore = new ArrayList<>();
                        lore.add("");
                        lore.add(ChatColor.GRAY + "Players: " + i.getPlayers() + "/" + i.getMaxPlayers());
                        ItemStack item;
                        if (player.getServer().getName().equals(i.getName())) {
                            item = AlterItem.makeItem(Material.EMERALD_BLOCK, i.getName(), lore, (byte) 0, index);
                        } else {
                            item = AlterItem.makeItem(Material.IRON_BLOCK, i.getName(), lore, (byte) 0, index);
                        }
                        servers.add(item);
                        index++;
                    }
                }
            }
        }
        ItemStack[] items = new ItemStack[servers.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = servers.get(i);
        }
        return items;
    }

    public ItemStack[] getLobbyServers(Player player) {
        ArrayList<ItemStack> servers = new ArrayList<>();
        int index = 1;
        for (ServerInfo i : serverList) {
            if (index <= 54) {
                if (i.getName().startsWith("Lobby")) {
                    if (i.isOnline() && i.isCanConnect()) {
                        ArrayList<String> lore = new ArrayList<>();
                        lore.add("");
                        lore.add(ChatColor.GRAY + "Players: " + i.getPlayers() + "/" + i.getMaxPlayers());
                        ItemStack item;
                        if (player.getServer().getServerName().equals(i.getName())) {
                            item = AlterItem.makeItem(Material.EMERALD_BLOCK, i.getName(), lore, (byte) 0, index);
                        } else {
                            item = AlterItem.makeItem(Material.IRON_BLOCK, i.getName(), lore, (byte) 0, index);
                        }
                        servers.add(item);
                        index++;
                    }
                }
            }
        }
        ItemStack[] items = new ItemStack[servers.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = servers.get(i);
        }
        return items;
    }

    public void addServer(ServerInfo in) {
        serverList.add(in);
    }

    public boolean containsServer(ServerInfo in) {
        for (ServerInfo i : serverList) {
            if (i.getName().equals(in.getName())) {
                return true;
            }
        }
        return false;
    }

    public void updateServer(ServerInfo in) {
        if (containsServer(in)) {
            for (ServerInfo i : serverList) {
                if (i.getName().equals(in.getName())) {
                    i.setCanConnect(in.isCanConnect());
                    i.setPlayers(in.getPlayers());
                    i.setMaxPlayers(in.getMaxPlayers());
                    i.setOnline(in.isOnline());
                }
            }
        } else {
            addServer(in);
        }
    }
}
