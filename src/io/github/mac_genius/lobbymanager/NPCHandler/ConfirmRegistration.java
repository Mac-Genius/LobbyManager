package io.github.mac_genius.lobbymanager.NPCHandler;

import io.github.mac_genius.lobbymanager.database.ServerWhitelist;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.Plugin;

/**
 * Created by Mac on 10/22/2015.
 */
public class ConfirmRegistration implements Listener {
    private Plugin plugin;
    private Player player;

    public ConfirmRegistration(Plugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        String timeout = ChatColor.GREEN + "Registration cancelled.";
        plugin.getServer().getScheduler().runTaskLater(plugin, new CancelListener(this, player, timeout, plugin), 100);
    }

    @EventHandler
    public void playerClick(PlayerInteractEntityEvent event) {
        if (event.getPlayer() == player) {
            ServerWhitelist whitelist = new ServerWhitelist(plugin);
            whitelist.setWhitelistStatus(event.getPlayer().getUniqueId().toString(), 1);

        }
    }
}
