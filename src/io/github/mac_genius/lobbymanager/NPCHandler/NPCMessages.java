package io.github.mac_genius.lobbymanager.NPCHandler;

import io.github.mac_genius.lobbymanager.database.ServerWhitelist;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

/**
 * Created by Mac on 10/21/2015.
 */
public class NPCMessages {
    private MessageConfig messageConfig;
    private Plugin plugin;
    private Player player;

    public NPCMessages(MessageConfig messageConfig, Plugin plugin, Player player) {
        this.messageConfig = messageConfig;
        this.plugin = plugin;
        this.player = player;
    }

    public ArrayList<String> getMessages(String job) {
        String message = job;
        if (job.equalsIgnoreCase("vanilla_register")) {
             message = vanillaNPC(message);
        }
        ArrayList<String> list = new ArrayList<>(messageConfig.getConfig().getStringList(message));
        ArrayList<String> output = new ArrayList<>();
        for (String s : list) {
            output.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return output;
    }

    private String vanillaNPC(String job) {
        ServerWhitelist whitelist = new ServerWhitelist(plugin);
        String output = job + ".";
        int status = whitelist.getWhitelistStatus(player.getUniqueId().toString());
        if (status == 0) {
            plugin.getServer().getPluginManager().registerEvents(new ConfirmRegistration(plugin, player), plugin);
        }
        output += status + "";
        return output;
    }
}
