package io.github.mac_genius.lobbymanager;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.mac_genius.lobbymanager.Commands.Commands;
import io.github.mac_genius.lobbymanager.Commands.SafeStop;
import io.github.mac_genius.lobbymanager.Listeners.EventListeners;
import io.github.mac_genius.lobbymanager.NPCHandler.StopMovement;
import io.github.mac_genius.lobbymanager.SecondaryThreads.SecondaryThread;
import io.github.mac_genius.lobbymanager.SecondaryThreads.UpdatePetLoc;
import io.github.mac_genius.lobbymanager.SecondaryThreads.UpdateShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitScheduler;

public class LobbyManager extends JavaPlugin implements PluginMessageListener {
    private Plugin plugin = this;
    private ServerSettings settings;

    public void onEnable() {
        settings = new ServerSettings(plugin);
        this.getCommand("sm").setExecutor(new Commands(settings));
        this.getCommand("sstop").setExecutor(new SafeStop(settings));
        BukkitScheduler taskSchedule = Bukkit.getScheduler();
        taskSchedule.runTaskTimer(plugin, new UpdateShop(settings), 0, 3);
        taskSchedule.runTaskTimer(plugin, new StopMovement(settings), 0, 1);
        taskSchedule.runTaskTimer(settings.getPlugin(), new UpdatePetLoc(settings), 0, 1);
        SecondaryThread secondaryThread = new SecondaryThread(settings);
        getServer().getPluginManager().registerEvents(new EventListeners(settings), this);
        taskSchedule.runTaskTimer(plugin, secondaryThread, 0, 10);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
        this.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new ServerPortals(settings), 0, 20);
        getLogger().info("The plugin has been enabled.");
    }

    public void onDisable() {
        getLogger().info("The plugin has been disabled.");
    }


    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("GetServers")) {
            String[] serverList = in.readUTF().split(", ");
            settings.setServerList(serverList);
            ByteArrayDataOutput out;
            for (String s : serverList) {
                out = ByteStreams.newDataOutput();
                if (s.contains("Survival")) {
                    out.writeUTF("PlayerCount");
                    out.writeUTF(s);
                    player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                }
                if (s.contains("Zombie")) {
                    out.writeUTF("PlayerCount");
                    out.writeUTF(s);
                    player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                }
                if (s.contains("Lobby")) {
                    out.writeUTF("PlayerCount");
                    out.writeUTF(s);
                    player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                }
            }
        }
        if (subchannel.equals("PlayerCount")) {
            String server = in.readUTF(); // Name of server, as given in the arguments
            int players = in.readInt();
            if (settings.getPlayerCount().containsKey(server)) {
                settings.getPlayerCount().replace(server, players + "");
            }
            else {
                settings.getPlayerCount().put(server, players + "");
            }
        }
    }

}
