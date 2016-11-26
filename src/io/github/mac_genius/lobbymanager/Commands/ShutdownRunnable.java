package io.github.mac_genius.lobbymanager.Commands;

import io.github.mac_genius.lobbymanager.ServerSettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Mac on 10/25/2015.
 */
public class ShutdownRunnable implements Runnable {
    private ServerSettings settings;

    public ShutdownRunnable(ServerSettings settings) {
        this.settings = settings;
    }

    @Override
    public void run() {
        settings.getPlugin().getServer().broadcastMessage(ChatColor.BLUE + "[Announcement] " + ChatColor.WHITE + "The server is restarting.");
        ArrayList<Player> player = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (Player p : player) {
            p.getInventory().clear();
        }
        settings.getPlugin().getServer().shutdown();
    }
}
