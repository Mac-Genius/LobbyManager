package io.github.mac_genius.lobbymanager.SecondaryThreads;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.mac_genius.lobbymanager.ServerSettings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Mac on 4/17/2015.
 */
public class SecondaryThread implements Runnable {
    private ServerSettings settings;

    public SecondaryThread(ServerSettings settings) {
        this.settings = settings;
    }

    public synchronized void run() {
        ArrayList<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (Player player : onlinePlayers) {
            updatePlayers(player);
            outOfBounds(player);
            updateOnline(player, onlinePlayers.size());
        }
    }

    private void updatePlayers(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServers");
        player.sendPluginMessage(settings.getPlugin(), "BungeeCord", out.toByteArray());
    }

    private void outOfBounds(Player player) {
        if (player.getLocation().getY() < 0) {
            String coords = settings.getPlugin().getConfig().getString("coords");
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
    }

    private void updateOnline(Player player, int playerAmount) {
        Scoreboard scoreboard = player.getScoreboard();
        player.setHealth(20);
        player.getWorld().setThundering(false);
        player.getWorld().setWeatherDuration(1000000000);
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
}
