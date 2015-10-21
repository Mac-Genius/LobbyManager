package io.github.mac_genius.scoreboardmaster;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Mac on 5/8/2015.
 */
public class Commands implements CommandExecutor {
    private Plugin plugin;
    private Connection connection;

    public Commands(Plugin pluginin, Connection connectionin) {
        plugin = pluginin;
        connection = connectionin;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("sm")) {
            if (strings.length == 0 && commandSender.hasPermission("sm.help")) {
                commandSender.sendMessage(ChatColor.GREEN + "---------- ScoreboardMaster Help ----------");
                commandSender.sendMessage(ChatColor.GOLD + "/sm reload" + ChatColor.WHITE + " reloads the config");
                commandSender.sendMessage(ChatColor.GOLD + "/sm help" + ChatColor.WHITE + " commands");
                return true;
            }
            if (strings[0].equalsIgnoreCase("reload") && commandSender.hasPermission("sm.reload")) {
                plugin.reloadConfig();
                commandSender.sendMessage(ChatColor.GREEN + "[ScoreboardMaster] " + ChatColor.WHITE + "Refreshing database connection.");
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    plugin.getLogger().warning("No connections to close.");
                }

                try {
                    MySQLConnect connect = new MySQLConnect(plugin);
                    connection = connect.getServer(plugin);
                    commandSender.sendMessage(ChatColor.GREEN + "[ScoreboardMaster] " + ChatColor.WHITE + "Connected to the database!");
                }  catch (Exception e) {
                    commandSender.sendMessage(ChatColor.GREEN + "[ScoreboardMaster] " + ChatColor.RED + "Cannot establish a connection!");
                } finally {
                    commandSender.sendMessage(ChatColor.GREEN + "[ScoreboardMaster] " + ChatColor.WHITE + "Config reloaded!");
                }
                return true;
            }
        }
        return false;
    }
}
