package io.github.mac_genius.lobbymanager.database.SQLObjects;

/**
 * Created by Mac on 10/23/2015.
 */
public class PetMenuObject {
    private boolean hasCow;
    private boolean hasPig;
    private boolean hasSheep;

    public  PetMenuObject(boolean hasCow, boolean hasPig, boolean hasSheep) {
        this.hasCow = hasCow;
        this.hasPig = hasPig;
        this.hasSheep = hasSheep;
    }

    public boolean hasCow() {
        return hasCow;
    }

    public boolean hasPig() {
        return hasPig;
    }

    public boolean hasSheep() {
        return hasSheep;
    }
}
