package io.github.mac_genius.scoreboardmaster;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import org.fusesource.jansi.Ansi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class ScoreboardMaster extends JavaPlugin implements PluginMessageListener {
    Plugin plugin = this;
    MySQLConnect connect = new MySQLConnect(plugin);
    Connection connection;
    PreparedStatement playerTable;
    String[] serverList;
    HashMap<String, String> playerCount;
    HashMap<Player, Boolean> thrown;

    public void onEnable() {
        plugin.saveDefaultConfig();
        playerCount = new HashMap<>();
        thrown = new HashMap<>();
        try {
            connection = connect.getServer(plugin);
            playerTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Players(Id INT PRIMARY KEY AUTO_INCREMENT, Uuid VARCHAR(36), Tokoin INT)");
            playerTable.executeUpdate();
        }  catch (Exception e) {
            getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Cannot fetch player data!" + Ansi.ansi().fg(Ansi.Color.WHITE));
        } finally {
            try {
                if (playerTable != null) {
                    playerTable.close();
                }
            } catch (SQLException e) {
                getLogger().warning("playerTable Exception.");
            }
            this.getCommand("sm").setExecutor(new Commands(plugin, connection));
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            BukkitScheduler taskSchedule = Bukkit.getScheduler();
            SecondaryThread secondaryThread = new SecondaryThread(manager, plugin, playerCount);
            getServer().getPluginManager().registerEvents(new EventListeners(manager, taskSchedule, plugin, connection, playerCount, thrown), this);
            taskSchedule.runTaskTimerAsynchronously(plugin, secondaryThread, 0, 10);
            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
            this.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new ServerPortals(plugin, thrown), 0, 20);
            getLogger().info("The plugin has been enabled.");
        }
    }

    public void onDisable() {
        try {
            connection.close();
            getLogger().info("Database connection closed.");
        } catch (SQLException e) {
            getLogger().warning("Database error.");
        } catch (NullPointerException e) {
            getLogger().warning("No connections to close.");
        }
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
            serverList = in.readUTF().split(", ");
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
            if (playerCount.containsKey(server)) {
                playerCount.replace(server, players + "");
            }
            else {
                playerCount.put(server, players + "");
            }
        }
    }
}
