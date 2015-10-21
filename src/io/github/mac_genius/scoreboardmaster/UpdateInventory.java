package io.github.mac_genius.scoreboardmaster;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

/**
 * Created by Mac on 5/21/2015.
 */
public class UpdateInventory implements Runnable {
    Plugin plugin;

    public UpdateInventory(Plugin pluginIn) {
        plugin = pluginIn;
    }

    @Override
    public void run() {
        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
    }
}
