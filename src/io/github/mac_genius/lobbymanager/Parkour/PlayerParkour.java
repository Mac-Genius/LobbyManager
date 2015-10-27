package io.github.mac_genius.lobbymanager.Parkour;

import io.github.mac_genius.lobbymanager.ServerSettings;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Mac on 10/25/2015.
 */
public class PlayerParkour {
    private ServerSettings settings;
    private Player player;
    private boolean inParkour;
    private boolean inPractice;
    private ParkourCourse course;
    private int checkpoint;
    private int fails;
    private Location checkpointLoc;
    private long time;
    private int timerId;

    public PlayerParkour(ServerSettings settings, Player player) {
        this.settings = settings;
        this.player = player;
        inParkour = false;
    }

    public boolean isInParkour() {
        return inParkour;
    }

    public void setInParkour(boolean inParkour) {
        this.inParkour = inParkour;
    }

    public ParkourCourse getCourse() {
        return course;
    }

    public void setCourse(ParkourCourse course) {
        this.course = course;
    }

    public Player getPlayer() {
        return player;
    }

    public int getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(int checkpoint) {
        this.checkpoint = checkpoint;
    }

    public Location getCheckpointLoc() {
        return checkpointLoc;
    }

    public void setCheckpointLoc(Location checkpointLoc) {
        this.checkpointLoc = checkpointLoc;
    }

    public void startParkour(ParkourCourse course) {
        this.course = course;
        this.inParkour = true;
        this.checkpointLoc = course.getStart().getLocation();
        player.setAllowFlight(false);
        for (String s : getStartMessage()) {
            player.sendMessage(s);
        }
        BukkitTask task = settings.getPlugin().getServer().getScheduler().runTaskTimer(settings.getPlugin(), new Timer(this), 0 , 1);
        timerId = task.getTaskId();
    }

    public void finishParkour() {
        settings.getPlugin().getServer().getScheduler().cancelTask(timerId);
        for (String s : getFinishMessage()) {
            player.sendMessage(s);
        }
        this.inParkour = false;
        this.course = null;
        this.checkpoint = 0;
        this.checkpointLoc = null;
        this.fails = 0;
        this.time = 0;
    }

    public void quitParkour() {
        settings.getPlugin().getServer().getScheduler().cancelTask(timerId);
        player.sendMessage(ChatColor.BLUE + "[Parkour] " + ChatColor.WHITE + "You have quit " + this.getCourse().getName() + "!");
        player.sendMessage(ChatColor.BLUE + "[Parkour] " + ChatColor.WHITE + "You had " + this.fails + " retries this time.");
        this.inParkour = false;
        this.course = null;
        this.checkpoint = 0;
        this.checkpointLoc = null;
        this.fails = 0;
        this.time = 0;
    }

    public void resetPlayer() {
        this.checkpointLoc = course.getStart().getLocation();
        player.teleport(checkpointLoc);
        this.fails = 0;
        this.checkpoint = 0;
        player.sendMessage(ChatColor.BLUE + "[Parkour] " + ChatColor.WHITE + "You have been reset to the start of " + this.getCourse().getName() + ".");
    }

    private ArrayList<String> getStartMessage() {
        ArrayList<String> message = new ArrayList<>();
        message.add(ChatColor.GREEN + "" + ChatColor.BOLD + "=================================");
        message.add(ChatColor.GREEN + "");
        message.add(ChatColor.WHITE + "Welcome to the " + this.getCourse().getName() + "!");
        message.add(ChatColor.GREEN + "");
        message.add(ChatColor.GOLD + "" + ChatColor.BOLD + "/parkour reset" + ChatColor.RESET + "" + ChatColor.WHITE + " will reset you back to the beginning.");
        message.add(ChatColor.GOLD + "" + ChatColor.BOLD + "/parkour quit" + ChatColor.RESET + "" + ChatColor.WHITE + " will allow you to quit the parkour.");
        message.add(ChatColor.GREEN + "");
        message.add(ChatColor.WHITE + "Have fun!");
        message.add(ChatColor.GREEN + "");
        message.add(ChatColor.GREEN + "" + ChatColor.BOLD + "=================================");
        return message;
    }

    private ArrayList<String> getFinishMessage() {
        ArrayList<String> message = new ArrayList<>();
        message.add(ChatColor.GREEN + "" + ChatColor.BOLD + "=================================");
        message.add(ChatColor.GREEN + "");
        message.add(ChatColor.WHITE + "" + "You finished " + ChatColor.AQUA + ChatColor.BOLD + this.getCourse().getName() + "!");
        message.add(ChatColor.GREEN + "");
        message.add(ChatColor.WHITE + "It only took you " + ChatColor.GREEN + "" + ChatColor.BOLD + this.fails + ChatColor.WHITE + " retries to complete " + this.getCourse().getName() + "!");
        message.add(ChatColor.GREEN + "");
        message.add(ChatColor.WHITE + "Your time was " + ChatColor.GREEN + "" + ChatColor.BOLD + new DecimalFormat("00").format((time / 20) / 60) + "." + new DecimalFormat("00").format((time / 20) % 60) + new DecimalFormat(".000").format(((double)time / 20.0) - Math.floor((double)time / 20.0)));
        message.add(ChatColor.GREEN + "");
        message.add(ChatColor.GREEN + "" + ChatColor.BOLD + "=================================");
        return message;
    }

    public void updateCheckpoint(Location checkpoint) {
        this.checkpointLoc = checkpoint;
        this.checkpoint++;
        player.sendMessage(ChatColor.BLUE + "[Parkour] " + ChatColor.WHITE + "You have reached checkpoint " + this.getCheckpoint() + " out of " + this.getCourse().getCheckpoints() + "!");
    }

    public void incrementFails() {
        this.fails++;
    }

    public boolean isInPractice() {
        return inPractice;
    }

    public void incrementTime() {
        time++;
    }
}
