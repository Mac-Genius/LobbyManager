package io.github.mac_genius.lobbymanager.Listeners.ListenerExtensions;

import io.github.mac_genius.lobbymanager.Inventories.CompassInventory;
import io.github.mac_genius.lobbymanager.Inventories.LobbySelect;
import io.github.mac_genius.lobbymanager.Inventories.PreferenceMenus.PreferenceMenu;
import io.github.mac_genius.lobbymanager.Inventories.WhitelistMenus.WhitelistMenu;
import io.github.mac_genius.lobbymanager.LobbyManager;
import io.github.mac_genius.lobbymanager.ServerSettings;
import org.bukkit.GameMode;
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
                LobbyManager.getSingleton().getServer().getPluginManager().registerEvents(new CompassInventory(settings, player), LobbyManager.getSingleton());
            } else if (player.hasPermission("lobbymanager.whitelist") && player.getInventory().getHeldItemSlot() == 2) {
                settings.getPlugin().getServer().getPluginManager().registerEvents(new WhitelistMenu(settings, player), settings.getPlugin());
            } else if (player.hasPermission("lobbymanager.whitelist") && player.getInventory().getHeldItemSlot() == 1) {
                settings.getPlugin().getServer().getPluginManager().registerEvents(new LobbySelect(player), settings.getPlugin());
            } else if (player.getInventory().getHeldItemSlot() == 8) {
                settings.getPlugin().getServer().getPluginManager().registerEvents(new PreferenceMenu(settings, player), settings.getPlugin());
            }
        }
    }
}
