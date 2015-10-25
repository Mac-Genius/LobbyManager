package io.github.mac_genius.lobbymanager.database;

import io.github.mac_genius.lobbymanager.ServerSettings;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.fusesource.jansi.Ansi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Mac on 10/21/2015.
 */
public class NPCList {
    private ServerSettings settings;
    private SQLConnect connect;

    public NPCList(ServerSettings settings) {
        this.settings = settings;
        connect = new SQLConnect(settings);
    }

    public void addNPC(Entity entity, String job) {
        Connection connection = connect.getConnection();
        String uuid = entity.getUniqueId().toString();
        try {
            PreparedStatement fetch = connection.prepareStatement("SELECT * FROM NPCList WHERE Uuid='" + uuid + "'");
            ResultSet result = fetch.executeQuery();
            String fetchUUID = "";
            while (result.next()) {
                fetchUUID = result.getString(2);
            }
            if (fetchUUID.equals("")) {
                addNPCToList(uuid, job, connection);
            }
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Adding the npc failed." + Ansi.ansi().fg(Ansi.Color.WHITE));
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "An error occurred when closing the connection." + Ansi.ansi().fg(Ansi.Color.WHITE));
            }
        }
    }

    private void addNPCToList(String uuid,String job, Connection connection) {
        try {
            PreparedStatement add = connection.prepareStatement("INSERT INTO NPCList(Uuid, Job) VALUES(?, ?)");
            add.setString(1, uuid);
            add.setString(2, job);
            add.executeUpdate();
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Adding the npc failed." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public boolean exists(Entity entity) {
        Connection connection = connect.getConnection();
        String uuid = entity.getUniqueId().toString();
        try {
            PreparedStatement fetch = connection.prepareStatement("SELECT * FROM NPCList WHERE Uuid='" + uuid + "'");
            ResultSet result = fetch.executeQuery();
            String fetchUUID = "";
            while (result.next()) {
                fetchUUID = result.getString(2);
            }
            if (fetchUUID.equals("")) {
                connection.close();
                return false;
            } else {
                connection.close();
                return true;
            }
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Adding the npc failed." + Ansi.ansi().fg(Ansi.Color.WHITE));
            return false;
        }
    }

    public void removeNPC(Entity entity) {
        Connection connection = connect.getConnection();
        String uuid = entity.getUniqueId().toString();
        try {
            PreparedStatement remove = connection.prepareStatement("DELETE FROM NPCList WHERE Uuid='" + uuid + "'");
            remove.executeUpdate();
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not remove the specified npc." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public String getJob(Entity entity) {
        String job = "";
        Connection connection = connect.getConnection();
        try {
            PreparedStatement fetch = connection.prepareStatement("SELECT * FROM NPCList WHERE Uuid='" + entity.getUniqueId().toString() + "'");
            ResultSet results = fetch.executeQuery();
            while (results.next()) {
                job = results.getString(3);
            }
            connection.close();
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not fetch the job for the entity." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
        return job;
    }

    public void setJob(Entity entity, String job) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement setJob = connection.prepareStatement("UPDATE NPCList SET Job='" + job + "' WHERE Uuid='" + entity.getUniqueId().toString() + "'");
            setJob.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not set the job for the entity." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }
}
