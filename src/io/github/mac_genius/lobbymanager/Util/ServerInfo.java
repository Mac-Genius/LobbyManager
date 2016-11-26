package io.github.mac_genius.lobbymanager.Util;

import java.util.Objects;

/**
 * Created by Mac on 3/21/2016.
 */
public class ServerInfo implements Comparable<ServerInfo> {
    private String name;
    private boolean online;
    private boolean canConnect;
    private int players;
    private int maxPlayers;

    public ServerInfo(String name, boolean online, boolean canConnect, int players, int maxPlayers) {
        this.name = name;
        this.online = online;
        this.canConnect = canConnect;
        this.players = players;
        this.maxPlayers = maxPlayers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isCanConnect() {
        return canConnect;
    }

    public void setCanConnect(boolean canConnect) {
        this.canConnect = canConnect;
    }

    public int getPlayers() {
        return players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ServerInfo) {
            return ((ServerInfo) o).getName().equals(name);
        }
        return false;
    }

    @Override
    public int compareTo(ServerInfo o) {
        return name.compareTo(o.getName());
    }
}
