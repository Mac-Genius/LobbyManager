package io.github.mac_genius.lobbymanager.Listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.mac_genius.lobbymanager.Inventories.CompassInventory;
import io.github.mac_genius.lobbymanager.Inventories.PreferenceMenus.PreferenceMenu;
import io.github.mac_genius.lobbymanager.Inventories.WhitelistMenus.WhitelistMenu;
import io.github.mac_genius.lobbymanager.Inventories.LobbyInventory;
import io.github.mac_genius.lobbymanager.KillRunner;
import io.github.mac_genius.lobbymanager.Listeners.ListenerExtensions.PlayerInitializer;
import io.github.mac_genius.lobbymanager.Listeners.ListenerExtensions.UpdateInvisible;
import io.github.mac_genius.lobbymanager.NPCHandler.MessageConfig;
import io.github.mac_genius.lobbymanager.NPCHandler.NPCMessages;
import io.github.mac_genius.lobbymanager.ScoreboardHandler.ScoreboardSetup;
import io.github.mac_genius.lobbymanager.database.NPCList;
import io.github.mac_genius.lobbymanager.database.Preferences;
import io.github.mac_genius.lobbymanager.database.SQLObjects.PlayerPreference;
import io.github.mac_genius.lobbymanager.database.ServerWhitelist;
import io.github.mac_genius.lobbymanager.database.TokoinUpdater;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
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
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;
import org.fusesource.jansi.Ansi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class EventListeners implements Listener {
    private ScoreboardManager manager;
    private BukkitScheduler schedule;
    private Plugin plugin;
    private HashMap<String, String> playerCount;
    private HashMap<Player, Boolean> thrown;
    private HashMap<Entity, String> npcs;
    private MessageConfig messageConfig;

    public EventListeners(ScoreboardManager managerIn, BukkitScheduler scheduleIn, Plugin pluginIn, HashMap<String, String> playerCountIn, HashMap<Player, Boolean> thrownIn, HashMap<Entity, String> npcs, MessageConfig messageConfig) {
        manager = managerIn;
        schedule = scheduleIn;
        plugin = pluginIn;
        playerCount = playerCountIn;
        thrown = thrownIn;
        this.npcs = npcs;
        this.messageConfig = messageConfig;
    }

    @EventHandler
    public void rightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        try {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (event.getItem().getType() == Material.COMPASS) {
                    CompassInventory compassInventory = new CompassInventory(plugin, player, playerCount);
                    player.openInventory(compassInventory.getInventory(player));
                } else if (event.getItem().getType() == Material.BOOK) {
                    plugin.getServer().getPluginManager().registerEvents(new WhitelistMenu(plugin, player), plugin);
                } else if (event.getItem().getType() == Material.ARMOR_STAND) {
                    plugin.getServer().getPluginManager().registerEvents(new PreferenceMenu(plugin, player), plugin);
                }
            }
            if (player.getPassenger() != null && event.getAction() == Action.LEFT_CLICK_AIR) {
                throwEntity(event.getPlayer(), event.getPlayer().getPassenger());
            }
        } catch (NullPointerException e) {
            plugin.getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Error with PlayerInteractEvent." + Ansi.ansi().fg(Ansi.Color.WHITE));
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
        event.setJoinMessage("");
        new PlayerInitializer(plugin, player, thrown, manager);
        new UpdateInvisible(plugin, player);
    }

    @EventHandler
     public void inventoryReact(InventoryClickEvent event) {
        if (event.getWhoClicked().getOpenInventory().getTitle().equals("Server Menu:")) {
            try {
                if (event.getCurrentItem().getType() == Material.DIAMOND_PICKAXE) {
                    ServerWhitelist whitelist = new ServerWhitelist(plugin);
                    if (whitelist.getWhitelisted(event.getWhoClicked().getUniqueId().toString())) {
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("Connect");
                        out.writeUTF("Survival-1");
                        ((Player) event.getWhoClicked()).sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                        ((Player) event.getWhoClicked()).sendMessage("Sending you to Survival!");
                    } else {
                        NPCMessages messages = new NPCMessages(messageConfig, plugin, (Player) event.getWhoClicked());
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
        if (npcs.containsKey(event.getRightClicked())) {
            NPCList list = new NPCList(plugin);
            NPCMessages npcMessages = new NPCMessages(messageConfig, plugin, event.getPlayer());
            ArrayList<String> messages = npcMessages.getMessages(list.getJob(event.getRightClicked()));
            for (String s : messages) {
                event.getPlayer().sendMessage(s);
            }
            event.setCancelled(true);
        }
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
        if (stackerIn instanceof Player) {
            PlayerPreference preferences = new Preferences(plugin).getPreferences(((Player) stackerIn).getUniqueId().toString());
            if (!preferences.canStack()) {
                stackerIn.sendMessage(ChatColor.GREEN + "You are not playing stacker right now.");
                return;
            }
        }
        if (stackIn instanceof Player) {
            PlayerPreference preferences = new Preferences(plugin).getPreferences(((Player) stackIn).getUniqueId().toString());
            if (!preferences.arePlayersVisible() || !preferences.canStack()) {
                stackerIn.sendMessage(ChatColor.GREEN + ((Player) stackIn).getDisplayName() + " is not playing the stack game right now.");
                return;
            }
        }
        if (stackerIn == stackIn || npcs.containsKey(stackIn) || stackIn instanceof ArmorStand) {
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
        for (Player p : new ArrayList<>(thrown.keySet())) {
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