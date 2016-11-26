package io.github.mac_genius.lobbymanager.database;

import io.github.mac_genius.sqleconomy.event.UpdatePlayerBankEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mac on 5/7/2015.
 */
public class TokoinUpdater implements Listener {

    @EventHandler
    public void bankEvent(UpdatePlayerBankEvent event) {
        updateScoreboard((int) event.getBank().getBalance(), event.getPlayer());
    }

    public static void updateScoreboard(int amount, Player player) {
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
