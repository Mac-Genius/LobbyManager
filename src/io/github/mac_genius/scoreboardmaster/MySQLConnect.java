package io.github.mac_genius.scoreboardmaster;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.fusesource.jansi.Ansi;

import java.sql.*;

/**
 * Created by Mac on 5/7/2015.
 */
public class MySQLConnect {
    private Plugin plugin;
    public MySQLConnect (Plugin pluginIn) {
        plugin = pluginIn;
    }
    public Connection getServer(Plugin plugin) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = null;

        String url = "jdbc:mysql://" + plugin.getConfig().getString("ip address") + "/" + plugin.getConfig().getString("database");
        String user = plugin.getConfig().getString("user");
        String password = plugin.getConfig().getString("password");

        try {
            con = DriverManager.getConnection(url, user, password);

        } catch (SQLException ex) {
            Bukkit.getLogger().warning("Error brah");

        }
        return con;
    }
}
