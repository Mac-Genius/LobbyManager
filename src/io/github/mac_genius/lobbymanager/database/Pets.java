package io.github.mac_genius.lobbymanager.database;

import io.github.mac_genius.lobbymanager.ServerSettings;
import io.github.mac_genius.lobbymanager.database.SQLObjects.Pet;
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
public class Pets {
    private ServerSettings settings;
    private SQLConnect connect;

    public Pets(ServerSettings settings) {
        this.settings = settings;
        connect = new SQLConnect(settings);
    }

    public void addPet(Player player, String type) {
        Connection connection = connect.getConnection();
        String uuid = player.getUniqueId().toString();
        try {
            PreparedStatement fetch = connection.prepareStatement("SELECT * FROM Pets WHERE Owner='" + uuid + "' AND Type='" + type + "'");
            ResultSet results = fetch.executeQuery();
            String owner = "";
            String petType = "";
            while (results.next()) {
                owner = results.getString(2);
                petType = results.getString(4);
            }
            if (owner.equals("") || petType.equals("")) {
                addToTable(player, type);
            }
            connection.close();
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not fetch the pet info." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    private void addToTable(Player player, String type) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement add = connection.prepareStatement("INSERT INTO Pets(Owner, Name, Type) VALUES(?, ?, ?)");
            add.setString(1, player.getUniqueId().toString());
            add.setString(2, "");
            add.setString(3, type);
            add.executeUpdate();
            connection.close();
        } catch (SQLException c) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not add the pet to the list." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public Pet getPet(String owner, String type) {
        Connection connection = connect.getConnection();
        Pet pet = null;
        try {
            PreparedStatement fetch = connection.prepareStatement("SELECT * FROM Pets WHERE Owner='" + owner + "' AND Type='" + type + "'");
            ResultSet results = fetch.executeQuery();
            String owneruuid = "";
            String name = "";
            String petType = "";
            while (results.next()) {
                owneruuid = results.getString(2);
                name = results.getString(3);
                petType = results.getString(4);
                pet = new Pet(owneruuid, name, petType);
            }
            connection.close();
            return pet;
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not fetch the pet info." + Ansi.ansi().fg(Ansi.Color.WHITE));
            return null;
        }
    }

    public void setName(String owner, String type, String name) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement fetch = connection.prepareStatement("UPDATE Pets SET Name='" + name + "' WHERE Owner='" + owner + "' AND Type='" + type + "'");
            fetch.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not fetch the pet info." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }
}
