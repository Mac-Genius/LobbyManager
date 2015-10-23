package io.github.mac_genius.lobbymanager;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;

public class KillRunner implements Runnable {
    Player killer;
    private final Scoreboard killerScore;

    public KillRunner(EntityDeathEvent eventIn) {
        killer = eventIn.getEntity().getKiller();
        killerScore = killer.getScoreboard();
    }

    public void run() {
        synchronized(killerScore) {
            // List of displayed strings on the scoreboard.
            ArrayList<String> list = new ArrayList<>(killerScore.getEntries());
            Score killScore = null;
            // Prints out the list to the player
            for(String e : list) {
                ArrayList<Score> scoreList = new ArrayList<>(killerScore.getScores(e));
                for (Score s : scoreList) {
                    if (s.getScore() == 3) {
                        killScore = s;
                    }
                }
            }

            assert killScore != null;
            int playerKills = Integer.parseInt(killScore.getEntry());
            killerScore.resetScores(killScore.getEntry());
            playerKills++;
            killerScore.getObjective("1").getScore(playerKills + "").setScore(3);
        }
    }
}