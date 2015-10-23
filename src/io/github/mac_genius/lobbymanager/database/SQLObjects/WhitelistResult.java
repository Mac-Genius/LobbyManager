package io.github.mac_genius.lobbymanager.database.SQLObjects;

/**
 * Created by Mac on 10/22/2015.
 */
public class WhitelistResult {
    private String name;
    private String uuid;
    private int status;

    public WhitelistResult(String name, String uuid, int status) {
        this.name = name;
        this.uuid = uuid;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }
}
