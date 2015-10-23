package io.github.mac_genius.lobbymanager;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.mac_genius.lobbymanager.Commands.Commands;
import io.github.mac_genius.lobbymanager.Listeners.EventListeners;
import io.github.mac_genius.lobbymanager.NPCHandler.MessageConfig;
import io.github.mac_genius.lobbymanager.NPCHandler.StopMovement;
import io.github.mac_genius.lobbymanager.database.NPCList;
import io.github.mac_genius.lobbymanager.database.SQLConnect;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import org.fusesource.jansi.Ansi;

import java.util.ArrayList;
import java.util.HashMap;

public class LobbyManager extends JavaPlugin implements PluginMessageListener {
    private Plugin plugin = this;
    private SQLConnect sqlConnect;
    private String[] serverList;
    private HashMap<String, String> playerCount;
    private HashMap<Player, Boolean> thrown;
    private HashMap<Entity, String> npcs;
    private HashMap<Entity, ArrayList<ArmorStand>> npcTags;
    private MessageConfig messageConfig;

    public void onEnable() {
        plugin.saveDefaultConfig();
        messageConfig = new MessageConfig(plugin);
        messageConfig.saveDefaultConfig();
        playerCount = new HashMap<>();
        thrown = new HashMap<>();
        sqlConnect = new SQLConnect(plugin);
        if (sqlConnect.testConnection()) {
            sqlConnect.databaseSetup();
            plugin.getLogger().info(Ansi.ansi().fg(Ansi.Color.GREEN) + "Connected to the database!" + Ansi.ansi().fg(Ansi.Color.WHITE));
        } else {
            plugin.getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not connect to the database!" + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
        npcs = npcList();
        npcTags = getNpcTags();
        this.getCommand("sm").setExecutor(new Commands(plugin, messageConfig, npcs, npcTags));
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        BukkitScheduler taskSchedule = Bukkit.getScheduler();
        taskSchedule.runTaskTimer(plugin, new StopMovement(npcs), 0, 1);
        SecondaryThread secondaryThread = new SecondaryThread(manager, plugin, playerCount);
        getServer().getPluginManager().registerEvents(new EventListeners(manager, taskSchedule, plugin, playerCount, thrown, npcs, messageConfig), this);
        taskSchedule.runTaskTimerAsynchronously(plugin, secondaryThread, 0, 10);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
        this.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new ServerPortals(plugin, thrown, messageConfig), 0, 20);
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

    public HashMap<Entity, String> npcList() {
        NPCList list = new NPCList(plugin);
        HashMap<Entity, String> npc = new HashMap<>();
        for (Entity e : Bukkit.getServer().getWorld("world").getEntities()) {
            if (list.exists(e)) {
                npc.put(e, list.getJob(e));
            }
        }
        return npc;
    }

    public HashMap<Entity, ArrayList<ArmorStand>> getNpcTags() {
        HashMap<Entity, ArrayList<ArmorStand>> output = new HashMap<>();
        for (Entity e : npcs.keySet()) {
            ArrayList<ArmorStand> armorStands = new ArrayList<>();
            for (Entity c : e.getNearbyEntities(0.5, 2, 0.5)) {
                if (c instanceof ArmorStand) {
                    armorStands.add((ArmorStand) c);
                }
            }
            output.put(e, armorStands);
        }
        return output;
    }
}
