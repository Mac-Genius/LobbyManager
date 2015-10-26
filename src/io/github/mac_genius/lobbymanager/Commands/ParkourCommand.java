package io.github.mac_genius.lobbymanager.Commands;

import io.github.mac_genius.lobbymanager.ServerSettings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Mac on 10/25/2015.
 */
public class ParkourCommand implements CommandExecutor {
    private ServerSettings settings;

    public ParkourCommand(ServerSettings settings) {
        this.settings = settings;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (command.getName().equalsIgnoreCase("parkour")) {
                if (strings[0] != null) {
                    if (strings[0].equalsIgnoreCase("reset")) {
                        settings.getParkour().get(player).resetPlayer();
                        return true;
                    } else if (strings[0].equalsIgnoreCase("quit")) {
                        settings.getParkour().get(player).quitParkour();
                        return true;
                    }
                }
                player.sendMessage(ChatColor.GREEN + "<---------- Parkour Help ---------->");
                player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "/parkour reset" + ChatColor.RESET + "" + ChatColor.WHITE + " resets you back to the beginning of the parkour.");
                player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "/parkour quit" + ChatColor.RESET + "" + ChatColor.WHITE + " lets you quit the parkour.");
                return true;
            }
        }
        return false;
    }
}
