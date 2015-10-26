package io.github.mac_genius.lobbymanager.Commands.CustomPrompts;

import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Mac on 10/26/2015.
 */
public class HoloText {
    private int amount;
    private ArrayList<String> lines;
    private double height;
    private Player player;

    public HoloText(Player player) {
        this.player = player;
        lines = new ArrayList<>();
    }

    public ArrayList<String> getLines() {
        return lines;
    }

    public double getHeight() {
        return height;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setLines(ArrayList<String> lines) {
        this.lines = lines;
    }

    public Player getPlayer() {
        return player;
    }
}
