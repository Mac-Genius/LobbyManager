package io.github.mac_genius.lobbymanager.Listeners;

import io.github.mac_genius.lobbymanager.Listeners.ListenerExtensions.InventoryClick;
import io.github.mac_genius.lobbymanager.Listeners.ListenerExtensions.PlayerInitializer;
import io.github.mac_genius.lobbymanager.Listeners.ListenerExtensions.UpdateInvisible;
import io.github.mac_genius.lobbymanager.LobbyManager;
import io.github.mac_genius.lobbymanager.NPCHandler.NPCMessages;
import io.github.mac_genius.lobbymanager.SecondaryThreads.Runnables.KillRunner;
import io.github.mac_genius.lobbymanager.ServerSettings;
import io.github.mac_genius.lobbymanager.database.NPCList;
import io.github.mac_genius.lobbymanager.database.Preferences;
import io.github.mac_genius.lobbymanager.database.SQLObjects.PlayerPreference;
import io.github.mac_genius.lobbymanager.database.TokoinUpdater;
import io.github.mac_genius.sqleconomy.cache.PlayerBank;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;
import org.fusesource.jansi.Ansi;

import java.util.ArrayList;

public class EventListeners implements Listener {
    private ServerSettings settings;

    public EventListeners(ServerSettings settings) {
        this.settings = settings;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void rightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        try {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                new InventoryClick(settings, event.getPlayer());
            }
            if (player.getPassenger() != null && event.getAction() == Action.LEFT_CLICK_AIR) {
                throwEntity(event.getPlayer(), event.getPlayer().getPassenger());
            }
        } catch (NullPointerException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Error with PlayerInteractEvent." + Ansi.ansi().fg(Ansi.Color.WHITE));
            e.printStackTrace();
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
                settings.getThrown().replace((Player) toThrow, true);
                toThrow.sendMessage(ChatColor.AQUA + "You were thrown by " + ChatColor.RESET + ((Player) thrower).getDisplayName() + ChatColor.AQUA + "!");
            }
        }
    }

    @EventHandler
    public synchronized void onEntityDeathEvent(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            settings.getPlugin().getServer().getScheduler().runTaskAsynchronously(settings.getPlugin(), new KillRunner(event));
        }
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) throws IllegalArgumentException {
        Player player = event.getPlayer();
        event.setJoinMessage("");
        new PlayerInitializer(settings, player);
        new UpdateInvisible(settings, player);
        LobbyManager.getSingleton().getServer().getScheduler().runTaskLaterAsynchronously(LobbyManager.getSingleton(), () -> {
            PlayerBank bank = LobbyManager.getSingleton().getEconomy().getAccount(event.getPlayer());
            TokoinUpdater.updateScoreboard((int) bank.getBalance(), event.getPlayer());
        }, 5);
    }

    @EventHandler
     public void inventoryReact(InventoryClickEvent event) {
        if (!event.getWhoClicked().hasPermission("lobbymanager.click")) {
            event.setCancelled(true);
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
        if (!event.getPlayer().hasPermission("lobbymanager.break")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        if (!event.getPlayer().hasPermission("lobbymanager.build")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void pickup(PlayerPickupItemEvent event) {
        if (!event.getPlayer().hasPermission("lobbymanager.drop")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void drop(PlayerDropItemEvent event) {
        if (!event.getPlayer().hasPermission("lobbymanager.drop")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void pickupPlayer(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (settings.getNpcs().containsKey(event.getRightClicked())) {
            NPCList list = new NPCList(settings);
            NPCMessages npcMessages = new NPCMessages(settings, event.getPlayer());
            ArrayList<String> messages = npcMessages.getMessages(list.getJob(event.getRightClicked()));
            for (String s : messages) {
                event.getPlayer().sendMessage(s);
            }
            event.setCancelled(true);
        }
        if (event.getRightClicked() instanceof Hanging) {
            return;
        }
        try {
            ArrayList<Entity> nearbyEntities = new ArrayList<>(event.getPlayer().getNearbyEntities(4, 4, 4));
            Entity closest = null;
            if (nearbyEntities.size() > 0) {
                closest = nearbyEntities.get(0);
            }
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
        if (stackerIn == stackIn || settings.getNpcs().containsKey(stackIn) || stackIn instanceof ArmorStand) {
            return;
        }
        if (stackerIn instanceof Player) {
            PlayerPreference preferences = new Preferences(settings).getPreferences(stackerIn.getUniqueId().toString());
            if (!preferences.canStack()) {
                stackerIn.sendMessage(ChatColor.GREEN + "You are not playing stacker right now.");
                return;
            }
        }
        if (stackIn instanceof Player) {
            PlayerPreference preferences = new Preferences(settings).getPreferences(stackIn.getUniqueId().toString());
            if (!preferences.arePlayersVisible() || !preferences.canStack()) {
                stackerIn.sendMessage(ChatColor.GREEN + ((Player) stackIn).getDisplayName() + " is not playing the stack game right now.");
                return;
            }
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
        for (Player p : new ArrayList<>(settings.getThrown().keySet())) {
            if (p == event.getPlayer()) {
                settings.getThrown().remove(p);
            }
        }
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        if (event.getFrom().getYaw() != event.getTo().getYaw() || event.getFrom().getPitch() != event.getTo().getPitch()) {
            for (Player p : settings.getThrown().keySet()) {
                if (p == event.getPlayer()) {
                    if (settings.getThrown().get(p)) {
                        settings.getThrown().replace(p, false);
                    }
                }
            }
        }
    }

    @EventHandler
    public void entityPortal(EntityPortalEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void breakPaintings(HangingBreakEvent event) {
        if (!event.getEntity().hasPermission("lobbymanager.break")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void stopGrowth(BlockGrowEvent event) {
        event.setCancelled(true);
    }
}