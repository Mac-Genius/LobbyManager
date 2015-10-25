package io.github.mac_genius.lobbymanager.Inventories.PreferenceMenus;

import io.github.mac_genius.lobbymanager.ServerSettings;
import io.github.mac_genius.lobbymanager.database.Preferences;
import io.github.mac_genius.lobbymanager.database.SQLObjects.PlayerPreference;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

/**
 * Created by Mac on 10/22/2015.
 */
public class PreferenceMenu implements Listener {
    private ServerSettings settings;
    private Player player;
    private Preferences pref;
    private PlayerPreference preferences;

    public PreferenceMenu(ServerSettings settings, Player player) {
        this.settings = settings;
        this.player = player;
        pref = new Preferences(settings);
        preferences = pref.getPreferences(player.getUniqueId().toString());
        setInventory();
    }

    public void setInventory() {
        Inventory inventory = Bukkit.createInventory(player, 18, "Preferences");
        inventory.setItem(1, getTimeIcon());
        inventory.setItem(3, getWeatherIcon());
        inventory.setItem(5, getVisbleIcon());
        inventory.setItem(7, getStckerIcon());
        inventory.setItem(10, getTimeButton());
        inventory.setItem(12, getWeatherButton());
        inventory.setItem(14, getVisbleButton());
        inventory.setItem(16, getStackerButton());
        player.openInventory(inventory);
    }

    private ItemStack getVisbleIcon() {
        String name = ChatColor.GREEN + "Player Visiblity";
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "Toggles the visibility of players.");
        return makeItem(Material.EYE_OF_ENDER, name, lore, (byte) 0);
    }

    private ItemStack getStckerIcon() {
        String name = ChatColor.GREEN + "Stacking Game";
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "Toggles where you can stack people and be stacked.");
        return makeItem(Material.POTION, name, lore, (byte) 0);
    }

    private ItemStack getTimeIcon() {
        String name = ChatColor.GREEN + "Lobby Time";
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "Toggles what time it is in the lobby.");
        return makeItem(Material.WATCH, name, lore, (byte) 0);
    }

    private ItemStack getWeatherIcon() {
        String name = ChatColor.GREEN + "Lobby Weather";
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "Toggles the weather in the lobby.");
        return makeItem(Material.DOUBLE_PLANT, name, lore, (byte) 0);
    }

    private ItemStack getVisbleButton() {
        String name;
        ArrayList<String> lore = new ArrayList<>();
        if (preferences.arePlayersVisible()) {
            name = ChatColor.GREEN + "Enabled";
            lore.add(ChatColor.WHITE + "You can see other players.");
            lore.add(ChatColor.WHITE + "Left click to toggle player visibility");
            return makeItem(Material.STAINED_GLASS_PANE, name, lore, (byte) 5);
        } else {
            name = ChatColor.RED + "Disabled";
            lore.add(ChatColor.WHITE + "Players are invisible.");
            lore.add(ChatColor.WHITE + "Left click to toggle player visibility");
            return makeItem(Material.STAINED_GLASS_PANE, name, lore, (byte) 14);
        }
    }

    private ItemStack getStackerButton() {
        String name;
        ArrayList<String> lore = new ArrayList<>();
        if (preferences.canStack()) {
            name = ChatColor.GREEN + "Enabled";
            lore.add(ChatColor.WHITE + "You can stack other players.");
            lore.add(ChatColor.WHITE + "Left click to toggle stacker");
            return makeItem(Material.STAINED_GLASS_PANE, name, lore, (byte) 5);
        } else {
            name = ChatColor.RED + "Disabled";
            lore.add(ChatColor.WHITE + "Stacker game is disabled.");
            lore.add(ChatColor.WHITE + "Left click to toggle stacker");
            return makeItem(Material.STAINED_GLASS_PANE, name, lore, (byte) 14);
        }
    }

    private ItemStack getTimeButton() {
        String name;
        ArrayList<String> lore = new ArrayList<>();
        if (preferences.getTime() == 0) {
            name = ChatColor.GREEN + "Sunrise";
            lore.add(ChatColor.WHITE + "What a lovely morning.");
            lore.add(ChatColor.WHITE + "Left click to toggle the time");
            return makeItem(Material.STAINED_GLASS_PANE, name, lore, (byte) 1);
        } else if (preferences.getTime() == 1) {
            name = ChatColor.GREEN + "Noon";
            lore.add(ChatColor.WHITE + "Halfway through the day.");
            lore.add(ChatColor.WHITE + "Left click to toggle the time");
            return makeItem(Material.STAINED_GLASS_PANE, name, lore, (byte) 4);
        } else if (preferences.getTime() == 2) {
            name = ChatColor.GREEN + "Sunset";
            lore.add(ChatColor.WHITE + "That is quite the sunset, eh?");
            lore.add(ChatColor.WHITE + "Left click to toggle the time");
            return makeItem(Material.STAINED_GLASS_PANE, name, lore, (byte) 14);
        } else {
            name = ChatColor.RED + "Night";
            lore.add(ChatColor.WHITE + "The best time to get work done.");
            lore.add(ChatColor.WHITE + "Left click to toggle the time");
            return makeItem(Material.STAINED_GLASS_PANE, name, lore, (byte) 15);
        }
    }

    private ItemStack getWeatherButton() {
        String name;
        ArrayList<String> lore = new ArrayList<>();
        if (preferences.isClear()) {
            name = ChatColor.GREEN + "Clear";
            lore.add(ChatColor.WHITE + "We can see clearly now,");
            lore.add(ChatColor.WHITE + "the rain is gone.");
            lore.add(ChatColor.WHITE + "Left click to toggle the weather");
            return makeItem(Material.STAINED_GLASS_PANE, name, lore, (byte) 3);
        } else {
            name = ChatColor.RED + "Raining";
            lore.add(ChatColor.WHITE + "Looks like you might want");
            lore.add(ChatColor.WHITE + "an umbrella today.");
            lore.add(ChatColor.WHITE + "Left click to toggle the weather");
            return makeItem(Material.STAINED_GLASS_PANE, name, lore, (byte) 7);
        }
    }

    private ItemStack makeItem(Material type, String name, ArrayList<String> lore, byte color) {
        ItemStack item = new ItemStack(type, 1, color);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void close(InventoryCloseEvent event) {
        if (event.getPlayer() == player) {
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler
    public void click(InventoryClickEvent event) {
        if (event.getWhoClicked() == player) {
            if (event.getSlot() == 10) {
                HandlerList.unregisterAll(this);
                long time = player.getPlayerTime();
                player.setPlayerTime(time + 6000, false);
                pref.toggleTime(player.getUniqueId().toString());
                settings.getPlugin().getServer().getPluginManager().registerEvents(new PreferenceMenu(settings, player), settings.getPlugin());
            }
            if (event.getSlot() == 12) {
                HandlerList.unregisterAll(this);
                if (preferences.isClear()) {
                    player.setPlayerWeather(WeatherType.DOWNFALL);
                } else {
                    player.setPlayerWeather(WeatherType.CLEAR);
                }
                pref.toggleWeather(player.getUniqueId().toString());
                settings.getPlugin().getServer().getPluginManager().registerEvents(new PreferenceMenu(settings, player), settings.getPlugin());
            }
            if (event.getSlot() == 14) {
                HandlerList.unregisterAll(this);
                if (preferences.arePlayersVisible()) {
                    ArrayList<Player> online = new ArrayList<>(Bukkit.getOnlinePlayers());
                    for (Player p : online) {
                        if (p != player) {
                            player.hidePlayer(p);
                        }
                    }
                } else {
                    ArrayList<Player> online = new ArrayList<>(Bukkit.getOnlinePlayers());
                    for (Player p : online) {
                        if (p != player) {
                            player.showPlayer(p);
                        }
                    }
                }
                pref.togglePlayersVisible(player.getUniqueId().toString());
                settings.getPlugin().getServer().getPluginManager().registerEvents(new PreferenceMenu(settings, player), settings.getPlugin());
            } if (event.getSlot() == 16) {
                HandlerList.unregisterAll(this);
                pref.toggleStacker(player.getUniqueId().toString());
                settings.getPlugin().getServer().getPluginManager().registerEvents(new PreferenceMenu(settings, player), settings.getPlugin());
            }
            event.setCancelled(true);
        }
    }
}
