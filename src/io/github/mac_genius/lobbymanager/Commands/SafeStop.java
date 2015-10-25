package io.github.mac_genius.lobbymanager.Commands;

import io.github.mac_genius.lobbymanager.ServerSettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Mac on 10/24/2015.
 */
public class SafeStop implements CommandExecutor {
    private ServerSettings settings;

    public SafeStop(ServerSettings settings) {
        this.settings = settings;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof ConsoleCommandSender || commandSender.isOp()) {
            if (command.getName().equalsIgnoreCase("sstop")) {
                settings.getPlugin().getServer().broadcastMessage(ChatColor.BLUE + "[Announcement] " + ChatColor.WHITE + "The server will be restarting in 30 seconds.");
                settings.getPlugin().getServer().getScheduler().runTaskLater(settings.getPlugin(), new ShutdownRunnable(settings), 600);
            }
            return true;
        }
        return false;
    }
}
