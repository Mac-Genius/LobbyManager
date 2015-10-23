package io.github.mac_genius.lobbymanager;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.mac_genius.lobbymanager.Inventories.CompassInventory;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Mac on 4/17/2015.
 */
public class SecondaryThread implements Runnable {
    private ScoreboardManager manager;
    private Plugin plugin;
    private HashMap<String, String> playerCount;

    public SecondaryThread(ScoreboardManager managerIn, Plugin pluginIn, HashMap<String, String> playerCountIn) {
        plugin = pluginIn;
        playerCount = playerCountIn;
        manager = managerIn;
    }

    public synchronized void run() {
        ArrayList<World> worlds = new ArrayList<>(Bukkit.getServer().getWorlds());
        ArrayList<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        int playerAmount = onlinePlayers.size();
        for (Player player : onlinePlayers) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("GetServers");
            player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
            Location block = player.getLocation();
            block.setY(block.getBlockY() - 1);
            Block test = block.getBlock();
            if (test.getType() != Material.AIR) {
                player.setAllowFlight(true);
            }
            if (player.getLocation().getY() < 0) {
                String coords = plugin.getConfig().getString("coords");
                Scanner scan = new Scanner(coords);
                double x = Double.parseDouble(scan.next());
                double y = Double.parseDouble(scan.next());
                double z = Double.parseDouble(scan.next());
                float yaw = 0;
                float pitch = 0;
                if (scan.hasNext()) {
                    yaw = Float.parseFloat(scan.next());
                }
                if (scan.hasNext()) {
                    pitch = Float.parseFloat(scan.next());
                }
                player.teleport(new Location(player.getWorld(), x, y, z, yaw, pitch));
            }
            Scoreboard scoreboard = player.getScoreboard();
            player.setHealth(20);
            player.getWorld().setThundering(false);
            player.getWorld().setWeatherDuration(1000000000);
            synchronized (scoreboard) {
                Objective o = scoreboard.getObjective(DisplaySlot.SIDEBAR);
                ArrayList<String> list = new ArrayList<>(scoreboard.getEntries());
                Score playersOnline = null;
                // Prints out the list to the player
                boolean ifFound = false;
                for (String e : list) {
                    ArrayList<Score> scoreList = new ArrayList<>(scoreboard.getScores(e));
                    for (Score s : scoreList) {
                        if (s.getScore() == 3) {
                            playersOnline = s;
                            ifFound = true;
                            break;
                        }
                    }
                    if (ifFound) {
                        break;
                    }
                }
                if (playersOnline != null) {
                    scoreboard.resetScores(playersOnline.getEntry());
                }
                o.getScore(playerAmount + "/" + Bukkit.getMaxPlayers()).setScore(3);
            }
            if (player.getOpenInventory().getTitle().equals("Server Menu:")) {
                CompassInventory compassInventory = new CompassInventory(plugin, player, playerCount);
                compassInventory.updatePlayerCount();
            }
        }
    }
}
