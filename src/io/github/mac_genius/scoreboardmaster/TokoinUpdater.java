package io.github.mac_genius.scoreboardmaster;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Mac on 5/7/2015.
 */
public class TokoinUpdater {
    PreparedStatement statement;
    ResultSet resultSet;
    PreparedStatement addPlayer;

    public void updateTokoin(Connection connectionIn, Player playerIn) {
        statement = null;
        resultSet = null;
        addPlayer = null;

        try {
            statement = connectionIn.prepareStatement("SELECT * FROM players");
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String uuid = resultSet.getString(2);
                if (playerIn.getUniqueId().toString().equals(uuid)) {
                    int amount = resultSet.getInt(3);
                    Scoreboard scoreboard = playerIn.getScoreboard();
                    synchronized (scoreboard) {
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
                        return;
                    }
                }
            }
            addPlayer = connectionIn.prepareStatement("INSERT INTO players(Uuid, Tokoin) VALUES(?, ?)");
            addPlayer.setString(1, playerIn.getUniqueId().toString());
            addPlayer.setInt(2, 5000);
            addPlayer.executeUpdate();
            Scoreboard scoreboard = playerIn.getScoreboard();
            synchronized (scoreboard) {
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
                o.getScore(5000 + "").setScore(0);
                return;
            }

        } catch (SQLException e) {
            Bukkit.getLogger().warning("Could not retrieve players.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                if (addPlayer != null) {
                    addPlayer.close();
                }
            } catch (SQLException e) {
                Bukkit.getLogger().warning("Error closing database connections.");
            }
        }
    }
}
