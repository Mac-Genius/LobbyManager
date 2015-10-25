package io.github.mac_genius.lobbymanager.NPCHandler;

import io.github.mac_genius.lobbymanager.ServerSettings;
import io.github.mac_genius.lobbymanager.database.ServerWhitelist;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

/**
 * Created by Mac on 10/21/2015.
 */
public class NPCMessages {
    private ServerSettings settings;
    private Player player;

    public NPCMessages(ServerSettings settings, Player player) {
        this.settings = settings;
        this.player = player;
    }

    public ArrayList<String> getMessages(String job) {
        String message = job;
        if (job.equalsIgnoreCase("vanilla_register")) {
             message = vanillaNPC(message);
        }
        ArrayList<String> list = new ArrayList<>(settings.getMessageConfig().getConfig().getStringList(message));
        ArrayList<String> output = new ArrayList<>();
        for (String s : list) {
            output.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return output;
    }

    private String vanillaNPC(String job) {
        ServerWhitelist whitelist = new ServerWhitelist(settings);
        String output = job + ".";
        int status = whitelist.getWhitelistStatus(player.getUniqueId().toString());
        if (status == 0) {
            settings.getPlugin().getServer().getPluginManager().registerEvents(new ConfirmRegistration(settings, player), settings.getPlugin());
        }
        output += status + "";
        return output;
    }
}
