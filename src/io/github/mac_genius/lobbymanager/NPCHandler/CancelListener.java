package io.github.mac_genius.lobbymanager.NPCHandler;

import io.github.mac_genius.lobbymanager.ServerSettings;
import io.github.mac_genius.lobbymanager.database.ServerWhitelist;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * Created by Mac on 10/22/2015.
 */
public class CancelListener implements Runnable {
    private Listener toCancel;
    private Player player;
    private String message;
    private ServerSettings settings;

    public CancelListener(Listener toCancel, Player player, String message, ServerSettings settings) {
        this.toCancel = toCancel;
        this.player = player;
        this.message = message;
        this.settings = settings;
    }

    @Override
    public void run() {
        HandlerList.unregisterAll(toCancel);
        ServerWhitelist whitelist = new ServerWhitelist(settings);
        if (!whitelist.getRegistered(player.getUniqueId().toString())) {
            player.sendMessage(message);
        }
    }
}
