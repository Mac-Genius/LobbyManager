package io.github.mac_genius.lobbymanager.Listeners.ListenerExtensions;

import io.github.mac_genius.lobbymanager.Inventories.CompassInventory;
import io.github.mac_genius.lobbymanager.Inventories.PreferenceMenus.PreferenceMenu;
import io.github.mac_genius.lobbymanager.Inventories.StoreMenus.BoneMenu;
import io.github.mac_genius.lobbymanager.Inventories.StoreMenus.StoreMenu;
import io.github.mac_genius.lobbymanager.Inventories.WhitelistMenus.WhitelistMenu;
import io.github.mac_genius.lobbymanager.ServerSettings;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by Mac on 10/23/2015.
 */
public class InventoryClick {
    private ServerSettings settings;
    private Player player;

    public InventoryClick(ServerSettings settings, Player player) {
        this.settings = settings;
        this.player = player;
        chooseMenu();
    }

    private void chooseMenu() {
        if (player.getGameMode() != GameMode.CREATIVE) {
            if (player.getInventory().getHeldItemSlot() == 0) {
                CompassInventory compassInventory = new CompassInventory(settings, player);
                player.openInventory(compassInventory.getInventory(player));
            } else if (player.hasPermission("lobbymanager.whitelist") && player.getInventory().getHeldItemSlot() == 1) {
                settings.getPlugin().getServer().getPluginManager().registerEvents(new WhitelistMenu(settings, player), settings.getPlugin());
            } else if (player.getInventory().getHeldItemSlot() == 8) {
                settings.getPlugin().getServer().getPluginManager().registerEvents(new PreferenceMenu(settings, player), settings.getPlugin());
            } else if (player.getInventory().getHeldItemSlot() == 4) {
                settings.getPlugin().getServer().getPluginManager().registerEvents(new StoreMenu(settings, player), settings.getPlugin());
            } else if (player.getInventory().getHeldItemSlot() == 5 && player.getInventory().getItemInHand() != null && player.getInventory().getItemInHand().getType() == Material.BONE) {
                settings.getPlugin().getServer().getPluginManager().registerEvents(new BoneMenu(settings, player), settings.getPlugin());
            }
        }
    }
}
