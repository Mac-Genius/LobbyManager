package io.github.mac_genius.lobbymanager.Listeners.ListenerExtensions;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.mac_genius.lobbymanager.Inventories.LobbyInventory;
import io.github.mac_genius.lobbymanager.Parkour.PlayerParkour;
import io.github.mac_genius.lobbymanager.ScoreboardHandler.ScoreboardSetup;
import io.github.mac_genius.lobbymanager.ServerSettings;
import io.github.mac_genius.lobbymanager.database.Preferences;
import io.github.mac_genius.lobbymanager.database.SQLObjects.PlayerPreference;
import io.github.mac_genius.lobbymanager.database.ServerWhitelist;
import io.github.mac_genius.lobbymanager.database.TokoinUpdater;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Mac on 10/22/2015.
 */
public class PlayerInitializer {
    private ServerSettings settings;
    private Player player;

    public PlayerInitializer(ServerSettings settings, Player player) {
        this.settings = settings;
        this.player = player;
        initializePlayer();
    }

    private void initializePlayer() {
        setupInventory();
        scoreboard();
        addDatabase();
        worldSettings();
        addCustom();
        spawn();
        preferences();
    }

    private void setupInventory() {
        new LobbyInventory(player);
    }

    private void addDatabase() {
        ServerWhitelist whitelist = new ServerWhitelist(settings);
        whitelist.addPlayer(player);
        Preferences preferences = new Preferences(settings);
        preferences.addPlayer(player);
        TokoinUpdater tokoinUpdater = new TokoinUpdater(settings, player);
        tokoinUpdater.addPlayer();
    }

    private void worldSettings() {
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(true);
        player.setFoodLevel(1000000000);
    }

    private void addCustom() {
        PlayerParkour parkour = new PlayerParkour(settings, player);
        settings.getParkour().put(player, parkour);
        settings.getThrown().put(player, false);
    }

    private void scoreboard() {
        ScoreboardSetup setup = new ScoreboardSetup(player, settings);
        setup.setScoreboard();
    }

    private void spawn() {
        try {
            if (settings.getPlugin().getConfig().getString("coords") == null || !settings.getPlugin().getConfig().getString("coords").equals("")) {
                String coords = settings.getPlugin().getConfig().getString("coords");
                Scanner scan = new Scanner(coords);
                double x = Double.parseDouble(scan.next());
                double y = Double.parseDouble(scan.next());
                double z = Double.parseDouble(scan.next());
                float yaw = 0;
                float pitch = 0;
                if (scan.hasNext()) {
                    yaw = Float.parseFloat(scan.next());
                }
                if (scan.hasNext()) {
                    pitch = Float.parseFloat(scan.next());
                }
                player.teleport(new Location(player.getWorld(), x, y, z, yaw, pitch));
            }
        } catch (NullPointerException e) {
            settings.getPlugin().getLogger().warning("You're using an outdated version of the config. Please delete it and restart.");
        }
    }

    private void preferences() {
        Preferences preferences = new Preferences(settings);
        PlayerPreference preference = preferences.getPreferences(player.getUniqueId().toString());
        if (!preference.arePlayersVisible()) {
            ArrayList<Player> online = new ArrayList<>(Bukkit.getOnlinePlayers());
            for (Player p : online) {
                if (p != player) {
                    player.hidePlayer(p);
                }
            }
        }
        if (preference.getTime() == 0) {
            player.setPlayerTime(0, false);
        } else if (preference.getTime() == 1) {
            player.setPlayerTime(6000, false);
        } else if (preference.getTime() == 2) {
            player.setPlayerTime(12000, false);
        } else {
            player.setPlayerTime(18000, false);
        }
        if (preference.isClear()) {
            player.setPlayerWeather(WeatherType.CLEAR);
        } else {
            player.setPlayerWeather(WeatherType.DOWNFALL);
        }
    }
}
