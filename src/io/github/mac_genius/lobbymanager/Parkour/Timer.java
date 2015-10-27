package io.github.mac_genius.lobbymanager.Parkour;

/**
 * Created by Mac on 10/27/2015.
 */
public class Timer implements Runnable {
    private PlayerParkour playerParkour;

    public Timer(PlayerParkour playerParkour) {
        this.playerParkour = playerParkour;
    }

    @Override
    public void run() {
        playerParkour.incrementTime();
    }
}
