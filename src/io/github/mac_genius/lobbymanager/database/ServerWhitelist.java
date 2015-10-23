package io.github.mac_genius.lobbymanager.database;

import io.github.mac_genius.lobbymanager.database.SQLObjects.WhitelistResult;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.fusesource.jansi.Ansi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Mac on 10/20/2015.
 */
public class ServerWhitelist {
    private Plugin plugin;
    private SQLConnect connect;

    public ServerWhitelist(Plugin plugin) {
        this.plugin = plugin;
        connect = new SQLConnect(this.plugin);
    }

    public void addPlayer(Player player) {
        Connection connection = connect.getConnection();
        String uuid = player.getUniqueId().toString();
        try {
            PreparedStatement fetch = connection.prepareStatement("SELECT * FROM Whitelist WHERE Uuid='" + uuid + "'");
            ResultSet results = fetch.executeQuery();
            String fetcheduuid = "";
            while (results.next()) {
                fetcheduuid = results.getString(2);
            }
            if (fetcheduuid.equals("")) {
                addToTable(player);
            }
            connection.close();
        } catch (SQLException e) {
            addToTable(player);
        }
    }

    private void addToTable(Player player) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement add = connection.prepareStatement("INSERT INTO Whitelist(Uuid, Registered, Username) VALUES(?, ?, ?)");
            add.setString(1, player.getUniqueId().toString());
            add.setInt(2, 0);
            add.setString(3, player.getDisplayName());
            add.executeUpdate();
            connection.close();
        } catch (SQLException c) {
            plugin.getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not add the player to the whitelist." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public boolean getRegistered(String uuid) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement fetch = connection.prepareStatement("SELECT * FROM Whitelist WHERE Uuid='" + uuid + "'");
            ResultSet results = fetch.executeQuery();
            int registered = 0;
            while (results.next()) {
                registered = results.getInt(3);
            }
            connection.close();
            if (registered == 1) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            plugin.getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not see if the player was registered." + Ansi.ansi().fg(Ansi.Color.WHITE));
            return false;
        }
    }

    public boolean getWhitelisted(String uuid) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement fetch = connection.prepareStatement("SELECT * FROM Whitelist WHERE Uuid='" + uuid + "'");
            ResultSet results = fetch.executeQuery();
            int registered = 0;
            while (results.next()) {
                registered = results.getInt(3);
            }
            connection.close();
            if (registered == 2) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            plugin.getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not see if the player was registered." + Ansi.ansi().fg(Ansi.Color.WHITE));
            return false;
        }
    }

    public void setWhitelistStatus(String uuid, int bool) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement register = connection.prepareStatement("UPDATE Whitelist SET Registered='" + bool + "' WHERE Uuid='" + uuid + "'");
            register.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            plugin.getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not update the player." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public int getWhitelistStatus(String uuid) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement fetch = connection.prepareStatement("SELECT * FROM Whitelist WHERE Uuid='" + uuid + "'");
            ResultSet results = fetch.executeQuery();
            int status = 0;
            while (results.next()) {
                status = results.getInt(3);
            }
            connection.close();
            return status;
        } catch (SQLException e) {
            plugin.getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not see if the player was registered." + Ansi.ansi().fg(Ansi.Color.WHITE));
            return 0;
        }
    }

    public boolean getBanned(String uuid) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement fetch = connection.prepareStatement("SELECT * FROM Whitelist WHERE Uuid='" + uuid + "'");
            ResultSet results = fetch.executeQuery();
            int registered = 0;
            while (results.next()) {
                registered = results.getInt(3);
            }
            connection.close();
            if (registered == 3) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            plugin.getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not see if the player was registered." + Ansi.ansi().fg(Ansi.Color.WHITE));
            return false;
        }
    }

    public ArrayList<WhitelistResult> getWaitlist() {
        Connection connection = connect.getConnection();
        ArrayList<WhitelistResult> waitlist = new ArrayList<>();
        try {
            PreparedStatement fetch = connection.prepareStatement("SELECT * FROM Whitelist WHERE Registered='1'");
            ResultSet results = fetch.executeQuery();
            while (results.next()) {
                WhitelistResult result = new WhitelistResult(results.getString(4), results.getString(2), results.getInt(3));
                waitlist.add(result);
            }
            connection.close();
            return waitlist;
        } catch (SQLException e) {
            plugin.getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not see if the player was registered." + Ansi.ansi().fg(Ansi.Color.WHITE));
            return null;
        }
    }

    public ArrayList<WhitelistResult> getWhitelist() {
        Connection connection = connect.getConnection();
        ArrayList<WhitelistResult> whitelist = new ArrayList<>();
        try {
            PreparedStatement fetch = connection.prepareStatement("SELECT * FROM Whitelist WHERE Registered='2'");
            ResultSet results = fetch.executeQuery();
            while (results.next()) {
                WhitelistResult result = new WhitelistResult(results.getString(4), results.getString(2), results.getInt(3));
                whitelist.add(result);
            }
            connection.close();
            return whitelist;
        } catch (SQLException e) {
            plugin.getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not see if the player was registered." + Ansi.ansi().fg(Ansi.Color.WHITE));
            return null;
        }
    }

    public ArrayList<WhitelistResult> getBanList() {
        Connection connection = connect.getConnection();
        ArrayList<WhitelistResult> banList = new ArrayList<>();
        try {
            PreparedStatement fetch = connection.prepareStatement("SELECT * FROM Whitelist WHERE Registered='3'");
            ResultSet results = fetch.executeQuery();
            while (results.next()) {
                WhitelistResult result = new WhitelistResult(results.getString(4), results.getString(2), results.getInt(3));
                banList.add(result);
            }
            connection.close();
            return banList;
        } catch (SQLException e) {
            plugin.getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not see if the player was registered." + Ansi.ansi().fg(Ansi.Color.WHITE));
            return null;
        }
    }

    public int waitlistSize() {
        return getWaitlist().size();
    }

    public int whitelistSize() {
        return getWhitelist().size();
    }

    public int bannedSize() {
        return getBanList().size();
    }
}
