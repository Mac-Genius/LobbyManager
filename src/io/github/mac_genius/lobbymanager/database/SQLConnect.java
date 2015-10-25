package io.github.mac_genius.lobbymanager.database;

import io.github.mac_genius.lobbymanager.ServerSettings;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.fusesource.jansi.Ansi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Mac on 10/20/2015.
 */
public class SQLConnect {
    private ServerSettings settings;

    public SQLConnect(ServerSettings settings) {
        this.settings = settings;
    }

    public void databaseSetup() {
        Connection connection = getConnection();
        if (connection != null) {
            try {
                PreparedStatement tokoins = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Players(Id INT PRIMARY KEY AUTO_INCREMENT, Uuid VARCHAR(36), Tokoin INT)");
                tokoins.executeUpdate();
                tokoins.close();
            } catch (SQLException e) {
                settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not create the tokoin table." + Ansi.ansi().fg(Ansi.Color.WHITE));
            }
            try {
                PreparedStatement whitelist = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Whitelist(Id INT PRIMARY KEY AUTO_INCREMENT, Uuid VARCHAR(36), Registered BOOLEAN, UserName VARCHAR(25))");
                whitelist.executeUpdate();
                whitelist.close();
            } catch (SQLException e) {
                settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not create the whitelist table." + Ansi.ansi().fg(Ansi.Color.WHITE));
            }
            try {
                PreparedStatement npcList = connection.prepareStatement("CREATE TABLE IF NOT EXISTS NPCList(Id INT PRIMARY KEY AUTO_INCREMENT, Uuid VARCHAR(36), Job VARCHAR(20))");
                npcList.executeUpdate();
                npcList.close();
            } catch (SQLException e) {
                settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not create the NPCList table." + Ansi.ansi().fg(Ansi.Color.WHITE));
            }
            try {
                PreparedStatement preferences = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Preferences(Id INT PRIMARY KEY AUTO_INCREMENT, Uuid VARCHAR(36), PlayersVisible BOOLEAN DEFAULT 1, Stacker BOOLEAN DEFAULT 1, Time TINYINT DEFAULT 1, Weather BOOLEAN DEFAULT 1)");
                preferences.executeUpdate();
                preferences.close();
            } catch (SQLException e) {
                settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not create the NPCList table." + Ansi.ansi().fg(Ansi.Color.WHITE));
            }
            try {
                PreparedStatement preferences = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Pets(Id INT PRIMARY KEY AUTO_INCREMENT, Owner VARCHAR(36), Name VARCHAR(25) NOT NULL, Type VARCHAR(25) NOT NULL)");
                preferences.executeUpdate();
                preferences.close();
            } catch (SQLException e) {
                settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not create the Pets table." + Ansi.ansi().fg(Ansi.Color.WHITE));
            }
            try {
                PreparedStatement preferences = connection.prepareStatement("CREATE TABLE IF NOT EXISTS PetMenu(Id INT PRIMARY KEY AUTO_INCREMENT, Uuid VARCHAR(36), Cow BOOLEAN DEFAULT 0, Pig BOOLEAN DEFAULT 0, Sheep BOOLEAN DEFAULT 0)");
                preferences.executeUpdate();
                preferences.close();
            } catch (SQLException e) {
                settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not create the Pets table." + Ansi.ansi().fg(Ansi.Color.WHITE));
            }
            try {
                connection.close();
            } catch (SQLException e) {
                settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not close the connection." + Ansi.ansi().fg(Ansi.Color.WHITE));
            }
        }
    }

    public Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not find the connection driver. Make sure it is installed and try again." + Ansi.ansi().fg(Ansi.Color.WHITE));
            return null;
        }
        Connection con = null;

        String url = "jdbc:mysql://" + settings.getPlugin().getConfig().getString("ip address") + "/" + settings.getPlugin().getConfig().getString("database");
        String user = settings.getPlugin().getConfig().getString("user");
        String password = settings.getPlugin().getConfig().getString("password");

        try {
            con = DriverManager.getConnection(url, user, password);

        } catch (SQLException ex) {
            Bukkit.getLogger().warning("Error brah");

        }
        return con;
    }

    public boolean testConnection() {
        Connection connection = getConnection();
        if (connection != null) {
            return true;
        } else {
            return false;
        }
    }
}
