package io.github.mac_genius.lobbymanager;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.mac_genius.lobbymanager.NPCHandler.MessageConfig;
import io.github.mac_genius.lobbymanager.NPCHandler.NPCMessages;
import io.github.mac_genius.lobbymanager.database.ServerWhitelist;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Mac on 5/21/2015.
 */
public class ServerPortals implements Runnable {
    private Plugin plugin;
    private HashMap<Player, Boolean> thrown;
    private MessageConfig messageConfig;

    public ServerPortals(Plugin pluginIn, HashMap<Player, Boolean> thrownIn, MessageConfig messageConfig) {
        plugin = pluginIn;
        thrown = thrownIn;
        this.messageConfig = messageConfig;
    }

    @Override
    public void run() {
        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (Player p : players) {
            boolean teleported = false;
            // p.sendMessage("X: " + p.getLocation().getX() + " Y: " + p.getLocation().getY() + " Z: " + p.getLocation().getZ());
            // Connect to Survival

            // Connect to Zombie World
            if (p.getLocation().getX() <= -219.0 && p.getLocation().getX() >= -219.9 && p.getLocation().getZ() >= -1243.5 && p.getLocation().getZ() <= -1240.3 && p.getLocation().getY() >= 112.0 && p.getLocation().getY() <= 115.0) {
                if (p.isInsideVehicle()) {
                    try {
                        if (plugin.getConfig().getString("coords") == null || !plugin.getConfig().getString("coords").equals("")) {
                            p.getVehicle().eject();
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
                            p.teleport(new Location(p.getWorld(), x, y, z, yaw, pitch));
                            return;
                        }
                    } catch (NullPointerException e) {
                        plugin.getLogger().warning("You're using an outdated version of the config. Please delete it and restart.");
                        return;
                    }
                }
                if (thrown.get(p)) {
                    Vector v = p.getVelocity();
                    v.setX(1);
                    v.setY(.5);
                    v.setZ(0);
                    p.setVelocity(v);
                    return;
                }
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF("Zombie-1");
                p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                p.sendMessage("Sending you to Zombie World!");
                teleported = true;
            }

            // Connect to Survival
            ServerWhitelist whitelist = new ServerWhitelist(plugin);

                if (p.getLocation().getX() <= -163.5 && p.getLocation().getX() >= -168.5 && p.getLocation().getZ() >= -1188.0 && p.getLocation().getZ() <= -1187.0 && p.getLocation().getY() >= 112.0 && p.getLocation().getY() <= 115.0) {
                    if (whitelist.getWhitelisted(p.getUniqueId().toString())) {
                        if (p.isInsideVehicle()) {
                            try {
                                if (plugin.getConfig().getString("coords") == null || !plugin.getConfig().getString("coords").equals("")) {
                                    p.getVehicle().eject();
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
                                    p.teleport(new Location(p.getWorld(), x, y, z, yaw, pitch));
                                    return;
                                }
                            } catch (NullPointerException e) {
                                plugin.getLogger().warning("You're using an outdated version of the config. Please delete it and restart.");
                                return;
                            }
                        }
                        if (thrown.get(p)) {
                            Vector v = p.getVelocity();
                            v.setX(0);
                            v.setY(1);
                            v.setZ(-2);
                            p.setVelocity(v);
                            return;
                        }
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("Connect");
                        out.writeUTF("Survival-1");
                        p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                        p.sendMessage("Sending you to Survival!");
                        teleported = true;
                    } else {
                        Vector v = p.getVelocity();
                        v.setX(0);
                        v.setY(1);
                        v.setZ(-2);
                        p.setVelocity(v);
                        NPCMessages messages = new NPCMessages(messageConfig, plugin, p);
                        if (whitelist.getBanned(p.getUniqueId().toString())) {
                            for (String s : messages.getMessages("vanilla_register")) {
                                p.sendMessage(s);
                            }
                        } else if (whitelist.getWhitelistStatus(p.getUniqueId().toString()) == 0) {
                            for (String s : messages.getMessages("van_go")) {
                                p.sendMessage(s);
                            }
                        } else {
                            for (String s : messages.getMessages("van_wait")) {
                                p.sendMessage(s);
                            }
                        }
                    }
                }


            // Connect to Survival Games
            if (p.getLocation().getX() <= -110.0 && p.getLocation().getX() >= -111.0 && p.getLocation().getZ() >= -1244.0 && p.getLocation().getZ() <= -1240.0 && p.getLocation().getY() >= 112.0 && p.getLocation().getY() <= 115.0) {

                if (p.isInsideVehicle()) {
                    try {
                        if (plugin.getConfig().getString("coords") == null || !plugin.getConfig().getString("coords").equals("")) {
                            p.getVehicle().eject();
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
                            p.teleport(new Location(p.getWorld(), x, y, z, yaw, pitch));
                            return;
                        }
                    } catch (NullPointerException e) {
                        plugin.getLogger().warning("You're using an outdated version of the config. Please delete it and restart.");
                        return;
                    }
                }
                if (thrown.get(p)) {
                    Vector v = p.getVelocity();
                    v.setX(-1);
                    v.setY(.5);
                    v.setZ(0);
                    p.setVelocity(v);
                    return;
                }
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF("SG-1");
                p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                p.sendMessage("Sending you to Survival Games!");
                teleported = true;
            }

            if (teleported) {
                ArrayList<Player> onlinePlayer = new ArrayList<>(Bukkit.getOnlinePlayers());
                for (Player pl : players) {
                    if (pl != p) {
                        pl.sendMessage(p.getName() + ChatColor.YELLOW + " has connected to another server.");
                    }
                }
            }
        }
    }
}
