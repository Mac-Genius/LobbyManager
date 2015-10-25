package io.github.mac_genius.lobbymanager.ScoreboardHandler;

import io.github.mac_genius.lobbymanager.ServerSettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

/**
 * Created by Mac on 4/17/2015.
 */
public class ScoreboardSetup {
    private Player player;
    private ServerSettings settings;
    public ScoreboardSetup(Player playerIn, ServerSettings settings) {
        player = playerIn;
        this.settings = settings;
    }

    public void setScoreboard() {
        Scoreboard scoreboard = settings.getManager().getNewScoreboard();
        player.setScoreboard(scoreboard);
        Objective o = scoreboard.registerNewObjective("1", "dummy");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.setDisplayName(" " + ChatColor.BOLD + "Welcome " + player.getDisplayName() + "!" + " ");
        o.getScore(ChatColor.BLACK + "").setScore(8);
        o.getScore(ChatColor.RED + "" + ChatColor.BOLD + "Server").setScore(7);
        o.getScore(Bukkit.getServerName()).setScore(6);
        o.getScore(ChatColor.BLUE + " ").setScore(5);
        o.getScore(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Online Players").setScore(4);
        o.getScore("0").setScore(3);
        o.getScore(ChatColor.RED + " ").setScore(2);
        o.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "Tokoins").setScore(1);
        o.getScore(ChatColor.WHITE + "0").setScore(0);
        player.setScoreboard(scoreboard);
    }
}
