package io.github.mac_genius.lobbymanager;

import io.github.mac_genius.lobbymanager.NPCHandler.MessageConfig;
import io.github.mac_genius.lobbymanager.database.NPCList;
import io.github.mac_genius.lobbymanager.database.SQLConnect;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import org.fusesource.jansi.Ansi;

import java.util.*;

/**
 * Created by Mac on 10/24/2015.
 */
public class ServerSettings {
    private Plugin plugin;
    private SQLConnect sqlConnect;
    private String[] serverList;
    private Map<String, String> playerCount;
    private Map<Player, Boolean> thrown;
    private Map<Entity, String> npcs;
    private Map<Entity, ArrayList<ArmorStand>> npcTags;
    private Map<Player, Entity> playerPets;
    private Map<Entity, Player> petOwners;
    private MessageConfig messageConfig;
    private ScoreboardManager manager;
    private BukkitScheduler taskSchedule;

    public ServerSettings(Plugin plugin) {
        this.plugin = plugin;
        setup();
    }

    private void setup() {
        setupPlugin();
        setupMessages();
        setupPlayerCount();
        setupThrown();
        setupSQL();
        setupNPC();
        setupPets();
    }

    private void setupPlugin() {
        plugin.saveDefaultConfig();
        manager = plugin.getServer().getScoreboardManager();
        taskSchedule = plugin.getServer().getScheduler();
    }

    private void setupMessages() {
        messageConfig = new MessageConfig(plugin);
        messageConfig.saveDefaultConfig();
    }

    private void setupPlayerCount() {
        playerCount = Collections.synchronizedMap(new HashMap<>());
    }

    private void setupThrown() {
        thrown = Collections.synchronizedMap(new HashMap<>());
    }

    private void setupSQL() {
        sqlConnect = new SQLConnect(this);
        if (sqlConnect.testConnection()) {
            sqlConnect.databaseSetup();
            plugin.getLogger().info(Ansi.ansi().fg(Ansi.Color.GREEN) + "Connected to the database!" + Ansi.ansi().fg(Ansi.Color.WHITE));
        } else {
            plugin.getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not connect to the database!" + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    private void setupNPC() {
        npcs = npcList();
        npcTags = getNpcTags();
    }

    private void setupPets() {
        playerPets = Collections.synchronizedMap(new HashMap<>());
        petOwners = Collections.synchronizedMap(new HashMap<>());
    }

    private Map<Entity, String> npcList() {
        NPCList list = new NPCList(this);
        Map<Entity, String> npc = Collections.synchronizedMap(new HashMap<>());
        for (Entity e : Bukkit.getServer().getWorld("world").getEntities()) {
            if (list.exists(e)) {
                npc.put(e, list.getJob(e));
            }
        }
        return npc;
    }

    private Map<Entity, ArrayList<ArmorStand>> getNpcTags() {
        Map<Entity, ArrayList<ArmorStand>> output = Collections.synchronizedMap(new HashMap<>());
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

    public Map<Entity, Player> getPetOwners() {
        return petOwners;
    }

    public Map<Entity, String> getNpcs() {
        return npcs;
    }

    public Map<Player, Boolean> getThrown() {
        return thrown;
    }

    public Map<Player, Entity> getPlayerPets() {
        return playerPets;
    }

    public Map<String, String> getPlayerCount() {
        return playerCount;
    }

    public MessageConfig getMessageConfig() {
        return messageConfig;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public SQLConnect getSqlConnect() {
        return sqlConnect;
    }

    public String[] getServerList() {
        return serverList;
    }

    public BukkitScheduler getTaskSchedule() {
        return taskSchedule;
    }

    public ScoreboardManager getManager() {
        return manager;
    }

    public void setServerList(String[] serverList) {
        this.serverList = serverList;
    }

    public Map<Entity, ArrayList<ArmorStand>> getNPCTags() {
        return npcTags;
    }
}
