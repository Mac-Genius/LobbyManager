package io.github.mac_genius.lobbymanager.Parkour;

import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * Created by Mac on 10/25/2015.
 */
public class ParkourCourse {
    private String name;
    private int checkpoints;
    private String difficulty;
    private Block start;
    private Block finish;

    public ParkourCourse(String name, int checkpoints, String difficulty, Location start, Location finish) {
        this.name = name;
        this.checkpoints = checkpoints;
        this.difficulty = difficulty;
        this.start = start.getBlock();
        this.finish = finish.getBlock();
    }

    public Block getStart() {
        return start;
    }

    public Block getFinish() {
        return finish;
    }

    public int getCheckpoints() {
        return checkpoints;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getName() {
        return name;
    }
}
