package io.github.mac_genius.scoreboardmaster;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class EventListeners implements Listener {
    ScoreboardManager manager;
    BukkitScheduler schedule;
    Plugin plugin;
    Connection connection;
    HashMap<String, String> playerCount;
    HashMap<Player, Boolean> thrown;

    public EventListeners(ScoreboardManager managerIn, BukkitScheduler scheduleIn, Plugin pluginIn, Connection connectionIn, HashMap<String, String> playerCountIn, HashMap<Player, Boolean> thrownIn) {
        manager = managerIn;
        schedule = scheduleIn;
        plugin = pluginIn;
        connection = connectionIn;
        playerCount = playerCountIn;
        thrown = thrownIn;
    }

    @EventHandler
    public void rightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        try {
            if (event.getAction() == Action.RIGHT_CLICK_AIR && event.getItem().getType() == Material.COMPASS || event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem().getType() == Material.COMPASS) {
                CompassInventory compassInventory = new CompassInventory(plugin, player, playerCount);
                player.openInventory(compassInventory.getInventory(player));
            }
            if (player.getPassenger() != null && event.getAction() == Action.LEFT_CLICK_AIR) {
                throwEntity(event.getPlayer(), event.getPlayer().getPassenger());
            }
        } catch (NullPointerException e) {
            return;
        }
    }

    public void throwEntity(Entity thrower, Entity toThrow) {
        Entity dontThrow = null;
        if (toThrow.getPassenger() != null) {
            dontThrow = toThrow.getPassenger();
        }
        if (dontThrow != null) {
            toThrow.eject();
            thrower.eject();
            thrower.setPassenger(dontThrow);
            toThrow.setVelocity(thrower.getLocation().getDirection().multiply(2.0));
            if (toThrow instanceof Player) {
                toThrow.sendMessage(ChatColor.AQUA + "You were thrown by " + ChatColor.RESET + ((Player) thrower).getDisplayName() + ChatColor.AQUA + "!");
            }
        }
        if (dontThrow == null){
            thrower.eject();
            toThrow.setVelocity(thrower.getLocation().getDirection().multiply(2.0));
            if (toThrow instanceof Player) {
                thrown.replace((Player) toThrow, true);
                toThrow.sendMessage(ChatColor.AQUA + "You were thrown by " + ChatColor.RESET + ((Player) thrower).getDisplayName() + ChatColor.AQUA + "!");
            }
        }
    }

    @EventHandler
    public synchronized void onEntityDeathEvent(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            schedule.runTaskAsynchronously(plugin, new KillRunner(event));
        }
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) throws IllegalArgumentException {
        Player player = event.getPlayer();
        player.getInventory().setHeldItemSlot(0);
        player.setGameMode(GameMode.SURVIVAL);
        event.setJoinMessage("");
        player.setAllowFlight(true);
        PlayerInventory inventory = new PlayerInventory(player);
        inventory.setPlayerInventory();
        thrown.put(player, false);

        // Sets the Scoreboard for a joining player
        ScoreboardSetup setup = new ScoreboardSetup(player, manager);
        setup.setScoreboard();
        TokoinUpdater tokoinUpdater = new TokoinUpdater();
        tokoinUpdater.updateTokoin(connection, event.getPlayer());
        try {
            if (plugin.getConfig().getString("coords") == null || !plugin.getConfig().getString("coords").equals("")) {
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
                player.teleport(new Location(event.getPlayer().getWorld(), x, y, z, yaw, pitch));
            }
        } catch (NullPointerException e) {
            plugin.getLogger().warning("You're using an outdated version of the config. Please delete it and restart.");
        }
        player.setFoodLevel(1000000000);
    }

    @EventHandler
     public void inventoryReact(InventoryClickEvent event) {
        if (event.getWhoClicked().getOpenInventory().getTitle().equals("Server Menu:")) {
            try {
                if (event.getCurrentItem().getType() == Material.DIAMOND_PICKAXE) {
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Connect");
                    out.writeUTF("Survival-1");
                    ((Player) event.getWhoClicked()).sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                    ((Player) event.getWhoClicked()).sendMessage("Sending you to Survival!");
                }
                if (event.getCurrentItem().getType() == Material.ROTTEN_FLESH) {
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Connect");
                    out.writeUTF("Zombie-1");
                    ((Player) event.getWhoClicked()).sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                    ((Player) event.getWhoClicked()).sendMessage("Sending you to Zombie World!");
                }
            } catch (NullPointerException e) {
                event.setCancelled(true);
            }
            event.setCancelled(true);
        }
        else {
            if (!event.getWhoClicked().isOp()) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void portals(PlayerPortalEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void entityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID || event.getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
            return;
        }
        else {
            event.setCancelled(true);
        }
    }

    @EventHandler
     public void blockBreak(BlockBreakEvent event) {
        if (!event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        if (!event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void pickup(PlayerPickupItemEvent event) {
        if (!event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void doubleJump(PlayerToggleFlightEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE && event.getPlayer().getAllowFlight()) {

            Vector jump = event.getPlayer().getVelocity();
            jump.setY(0);

            Vector facing = event.getPlayer().getLocation().getDirection();
            facing = facing.multiply(2.0);
            jump = jump.add(facing);
            event.getPlayer().setVelocity(jump);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.FIREWORK_BLAST, 10, 1);

            event.setCancelled(true);
            event.getPlayer().setAllowFlight(false);
        }
    }

    @EventHandler
    public void drop(PlayerDropItemEvent event) {
        if (!event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void pickupPlayer(PlayerInteractEntityEvent event) {
        try {
            ArrayList<Entity> nearbyEntities = new ArrayList<>(event.getPlayer().getNearbyEntities(4, 4, 4));
            Entity closest = nearbyEntities.get(0);
            for (Entity e : nearbyEntities) {
                if (event.getPlayer().hasLineOfSight(e)) {
                    if (event.getPlayer().getLocation().distance(e.getLocation()) <= 3.5) {
                        if (event.getPlayer().getPassenger() != null && event.getPlayer().getPassenger() != e || event.getPlayer().getPassenger() == null) {
                            if (event.getPlayer().getLocation().distance(closest.getLocation()) > event.getPlayer().getLocation().distance(e.getLocation())) {
                                closest = e;
                            }
                        }
                    }
                }
            }
            stack(event.getPlayer(), closest);
        } catch (NullPointerException e) {
            return;
        }
    }

    public void stack(Entity stackerIn, Entity stackIn) {
        if (stackerIn == stackIn) {
            return;
        }
        if (stackerIn.getPassenger() != null) {
            stack(stackerIn.getPassenger(), stackIn);
        }
        else {
            stackerIn.setPassenger(stackIn);
        }
    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent event) {
        event.setQuitMessage("");
        for (Player p : thrown.keySet()) {
            if (p == event.getPlayer()) {
                thrown.remove(p);
            }
        }
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        if (event.getFrom().getYaw() != event.getTo().getYaw() || event.getFrom().getPitch() != event.getTo().getPitch()) {
            for (Player p : thrown.keySet()) {
                if (p == event.getPlayer()) {
                    if (thrown.get(p)) {
                        thrown.replace(p, false);
                    }
                }
            }
        }
    }
}