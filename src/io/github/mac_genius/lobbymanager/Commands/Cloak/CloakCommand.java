package io.github.mac_genius.lobbymanager.Commands.Cloak;

import io.github.mac_genius.lobbymanager.ServerSettings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Mac on 10/28/2015.
 */
public class CloakCommand implements CommandExecutor {
    private ServerSettings settings;

    public CloakCommand(ServerSettings settings) {
        this.settings = settings;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("cloak")) {
            if (commandSender instanceof Player && commandSender.hasPermission("lobbymanager.cloak")) {
                if (strings.length > 0) {
                    if (strings[0] != null) {
                        settings.getPlugin().getServer().getScheduler().runTaskAsynchronously(settings.getPlugin(), new CloakRun((Player) commandSender, strings[0], settings));
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
