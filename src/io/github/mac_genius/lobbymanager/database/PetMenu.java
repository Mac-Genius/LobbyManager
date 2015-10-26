package io.github.mac_genius.lobbymanager.database;

import io.github.mac_genius.lobbymanager.ServerSettings;
import io.github.mac_genius.lobbymanager.database.SQLObjects.PetMenuObject;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.fusesource.jansi.Ansi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Mac on 10/23/2015.
 */
public class PetMenu {
    private ServerSettings settings;
    private SQLConnect connect;

    public PetMenu(ServerSettings settings) {
        this.settings = settings;
        connect = new SQLConnect(settings);
    }

    public void addMenu(Player player) {
        Connection connection = connect.getConnection();
        String uuid = player.getUniqueId().toString();
        try {
            PreparedStatement fetch = connection.prepareStatement("SELECT * FROM PetMenu WHERE Uuid='" + uuid + "'");
            ResultSet results = fetch.executeQuery();
            String fetchuuid = "";
            while (results.next()) {
                fetchuuid = results.getString(2);
            }
            if (fetchuuid.equals("")) {
                addToTable(player);
            }
            connection.close();
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not fetch the pet info." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    private void addToTable(Player player) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement add = connection.prepareStatement("INSERT INTO PetMenu(Uuid) VALUES(?)");
            add.setString(1, player.getUniqueId().toString());
            add.executeUpdate();
            connection.close();
        } catch (SQLException c) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not add the player to the PetMenu list." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public PetMenuObject getMenu(String uuid) {
        Connection connection = connect.getConnection();
        PetMenuObject menu = null;
        try {
            PreparedStatement fetch = connection.prepareStatement("SELECT * FROM PetMenu WHERE Uuid='" + uuid + "'");
            ResultSet results = fetch.executeQuery();
            boolean cow = false;
            boolean pig = false;
            boolean sheep = false;
            while (results.next()) {
                cow = results.getBoolean(3);
                pig = results.getBoolean(4);
                sheep = results.getBoolean(5);
                menu = new PetMenuObject(cow, pig, sheep);
            }
            connection.close();
            return menu;
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not fetch the pet info." + Ansi.ansi().fg(Ansi.Color.WHITE));
            return null;
        }
    }

    public void setCow(String uuid, int bool) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement register = connection.prepareStatement("UPDATE PetMenu SET Cow='" + bool + "' WHERE Uuid='" + uuid + "'");
            register.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not update the player." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public void setPig(String uuid, int bool) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement register = connection.prepareStatement("UPDATE PetMenu SET Pig='" + bool + "' WHERE Uuid='" + uuid + "'");
            register.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not update the player." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public void setSheep(String uuid, int bool) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement register = connection.prepareStatement("UPDATE PetMenu SET Sheep='" + bool + "' WHERE Uuid='" + uuid + "'");
            register.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not update the player." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public void unlockCow(String uuid) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement register = connection.prepareStatement("UPDATE PetMenu SET Cow='1' WHERE Uuid='" + uuid + "'");
            register.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not update the player." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public void unlockPig(String uuid) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement register = connection.prepareStatement("UPDATE PetMenu SET Pig='1' WHERE Uuid='" + uuid + "'");
            register.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not update the player." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public void unlockSheep(String uuid) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement register = connection.prepareStatement("UPDATE PetMenu SET Sheep='1' WHERE Uuid='" + uuid + "'");
            register.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not update the player." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public void unlockPet(String uuid, String pet) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement register = connection.prepareStatement("UPDATE PetMenu SET " + pet + "='1' WHERE Uuid='" + uuid + "'");
            register.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not update the player." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }
}
