package io.github.mac_genius.scoreboardmaster;

import org.bukkit.entity.Entity;

/**
 * Created by Mac on 6/6/2015.
 */
public class ThrowPlayer implements Runnable {
    private Entity thrower;
    private Entity beingThrown;
    private Entity dontThrow;

    public ThrowPlayer(Entity throwerIn, Entity beingThrownIn) {
        thrower = throwerIn;
        beingThrown = beingThrownIn;
    }

    @Override
    public void run() {

    }
}
