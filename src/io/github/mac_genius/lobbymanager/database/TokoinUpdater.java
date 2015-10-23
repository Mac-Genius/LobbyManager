package io.github.mac_genius.lobbymanager.database;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.fusesource.jansi.Ansi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Mac on 5/7/2015.
 */
public class TokoinUpdater {
    private Plugin plugin;
    private SQLConnect connect;
    private Player player;

    public TokoinUpdater(Plugin plugin, Player player) {
        this.plugin = plugin;
        connect = new SQLConnect(this.plugin);
        this.player = player;
    }

    public void addPlayer() {
        Connection connection = connect.getConnection();
        String uuid = player.getUniqueId().toString();
        try {
            PreparedStatement fetch = connection.prepareStatement("SELECT * FROM Players WHERE Uuid='" + uuid + "'");
            ResultSet results = fetch.executeQuery();
            String fetcheduuid = "";
            while (results.next()) {
                fetcheduuid = results.getString(2);
            }
            if (fetcheduuid.equals("")) {
                addToTable(uuid);
            }
            updateScoreboard(getTokoins());
            connection.close();
        } catch (SQLException e) {
            plugin.getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not add the player to database." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    private void addToTable(String UUID) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement add = connection.prepareStatement("INSERT INTO Players(Uuid, Tokoin) VALUES(?, ?)");
            add.setString(1, UUID);
            add.setInt(2, 5000);
            add.executeUpdate();
            connection.close();
        } catch (SQLException c) {
            plugin.getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not add the player to database." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public int getTokoins() {
        Connection connection = connect.getConnection();
        String uuid = player.getUniqueId().toString();
        int tokoins = 0;
        try {
            PreparedStatement fetch = connection.prepareStatement("SELECT * FROM Players WHERE Uuid='" + uuid + "'");
            ResultSet results = fetch.executeQuery();
            while (results.next()) {
                tokoins = results.getInt(3);
            }
            connection.close();
            return tokoins;
        } catch (SQLException e) {
            plugin.getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not add the player to database." + Ansi.ansi().fg(Ansi.Color.WHITE));
            return tokoins;
        }
    }

    public void setTokoins(int amount) {
        Connection connection = connect.getConnection();
        String uuid = player.getUniqueId().toString();
        try {
            PreparedStatement set = connection.prepareStatement("UPDATE Players SET Tokoin='" + amount + "' WHERE Uuid='" + uuid + "'");
            set.executeUpdate();
            updateScoreboard(getTokoins());
            connection.close();
        } catch (SQLException e) {
            plugin.getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not set the player's tokoin amount." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public void addTokoins(int amount) {
        Connection connection = connect.getConnection();
        String uuid = player.getUniqueId().toString();
        int total = getTokoins() + amount;
        try {
            PreparedStatement set = connection.prepareStatement("UPDATE Players SET Tokoin='" + total + "' WHERE Uuid='" + uuid + "'");
            set.executeUpdate();
            updateScoreboard(getTokoins());
            connection.close();
        } catch (SQLException e) {
            plugin.getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not set the player's tokoin amount." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    private void updateScoreboard(int amount) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective o = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        ArrayList<String> list = new ArrayList<>(scoreboard.getEntries());
        Score tokoinAmount = null;
        // Prints out the list to the player
        boolean ifFound = false;
        for (String e : list) {
            ArrayList<Score> scoreList = new ArrayList<>(scoreboard.getScores(e));
            for (Score s : scoreList) {
                if (s.getScore() == 0) {
                    tokoinAmount = s;
                    ifFound = true;
                    break;
                }
            }
            if (ifFound) {
                break;
            }
        }
        if (tokoinAmount != null) {
            scoreboard.resetScores(tokoinAmount.getEntry());
        }
        o.getScore(amount + "").setScore(0);
    }
}
