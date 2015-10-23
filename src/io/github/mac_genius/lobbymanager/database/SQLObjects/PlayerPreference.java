package io.github.mac_genius.lobbymanager.database.SQLObjects;

/**
 * Created by Mac on 10/22/2015.
 */
public class PlayerPreference {
    private boolean playersVisible;
    private boolean canStack;
    private int time;
    private boolean weather;

    public PlayerPreference(boolean playersVisible, boolean canStack, int time, boolean weather) {
        this.playersVisible = playersVisible;
        this.canStack = canStack;
        this.time = time;
        this.weather = weather;
    }

    public boolean canStack() {
        return canStack;
    }

    public boolean arePlayersVisible() {
        return playersVisible;
    }

    public int getTime() {
        return time;
    }

    public boolean isClear() {
        return weather;
    }
}
