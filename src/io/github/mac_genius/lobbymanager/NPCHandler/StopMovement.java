package io.github.mac_genius.lobbymanager.NPCHandler;

import io.github.mac_genius.lobbymanager.ServerSettings;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.HashMap;

/**
 * Created by Mac on 10/21/2015.
 */
public class StopMovement implements Runnable {
    private ServerSettings settings;

    public StopMovement(ServerSettings settings) {
        this.settings = settings;
    }

    @Override
    public void run() {
        for (Entity e : settings.getNpcs().keySet()) {
            Double directionX = e.getVelocity().getX() * -1;
            Double directionY = 0.0;
            Double directionZ = e.getVelocity().getZ() * -1;
            Vector opposite = new Vector(directionX, directionY, directionZ);
            opposite.add(e.getVelocity());
            e.setVelocity(opposite);
        }
    }
}
