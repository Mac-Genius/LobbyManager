package io.github.mac_genius.lobbymanager.SecondaryThreads;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.mac_genius.lobbymanager.Inventories.CompassInventory;
import io.github.mac_genius.lobbymanager.ServerSettings;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.*;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
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
            updateCompass(player);
            outOfBounds(player);
            doubleJump(player);
            updateOnline(player, onlinePlayers.size());
            updatePet(player);
        }
    }

    private void updatePlayers(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServers");
        player.sendPluginMessage(settings.getPlugin(), "BungeeCord", out.toByteArray());
    }

    private void outOfBounds(Player player) {
        if (settings.getParkour().get(player).isInParkour()) {
            if (settings.getParkour().get(player).getCheckpointLoc().getY() - player.getLocation().getY() > 5) {
                player.teleport(settings.getParkour().get(player).getCheckpointLoc());
                settings.getParkour().get(player).incrementFails();
            }
        }
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

    private void doubleJump(Player player) {
        Location block = player.getLocation();
        block.setY(block.getBlockY() - 1);
        Block test = block.getBlock();
        if (test.getType() != Material.AIR && !settings.getParkour().get(player).isInParkour()) {
            player.setAllowFlight(true);
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

    private void updateCompass(Player player) {
        if (player.getOpenInventory().getTitle().equals("Server Menu:")) {
            CompassInventory compassInventory = new CompassInventory(settings, player);
            compassInventory.updatePlayerCount();
        }
    }

    private void updatePet(Player player) {
        if (settings.getPlayerPets().containsKey(player)) {
            Entity pet = settings.getPlayerPets().get(player);
            if (pet instanceof Creature) {
                if (pet.getLocation().distance(player.getLocation()) > 8) {
                    ((Creature) pet).setTarget(player);
                }
            }
            if (pet.getLocation().distance(player.getLocation()) > 15) {
                Location location = player.getLocation();
                location.setY(location.getY() - 1);
                if (location.getBlock().getType() != Material.AIR) {
                    pet.teleport(player.getLocation());
                }
            }
        }
    }
}
