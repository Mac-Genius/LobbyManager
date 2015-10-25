package io.github.mac_genius.lobbymanager.database;

import io.github.mac_genius.lobbymanager.ServerSettings;
import io.github.mac_genius.lobbymanager.database.SQLObjects.PlayerPreference;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.fusesource.jansi.Ansi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Mac on 10/22/2015.
 */
public class Preferences {
    private ServerSettings settings;
    private SQLConnect connect;

    public Preferences(ServerSettings settings) {
        this.settings = settings;
        connect = new SQLConnect(settings);
    }

    public void addPlayer(Player player) {
        Connection connection = connect.getConnection();
        String uuid = player.getUniqueId().toString();
        try {
            PreparedStatement fetch = connection.prepareStatement("SELECT * FROM Preferences WHERE Uuid='" + uuid + "'");
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
            PreparedStatement add = connection.prepareStatement("INSERT INTO Preferences(Uuid) VALUES(?)");
            add.setString(1, player.getUniqueId().toString());
            add.executeUpdate();
            connection.close();
        } catch (SQLException c) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not add the player to the whitelist." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public PlayerPreference getPreferences(String uuid) {
        Connection connection = connect.getConnection();
        PlayerPreference preferences = null;
        try {
            PreparedStatement fetch = connection.prepareStatement("SELECT * FROM Preferences WHERE Uuid='" + uuid + "'");
            ResultSet results = fetch.executeQuery();
            while (results.next()) {
                int playersVisible = results.getInt(3);
                int canStack = results.getInt(4);
                int time = results.getInt(5);
                int weather = results.getInt(6);
                preferences = new PlayerPreference(playersVisible == 1, canStack == 1, time, weather == 1);
            }
            connection.close();
            return preferences;
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not add the player to the preference table." + Ansi.ansi().fg(Ansi.Color.WHITE));
            return null;
        }
    }

    public boolean arePlayersVisible(String uuid) {
        if (getPreferences(uuid) == null) {
            return true;
        } else return getPreferences(uuid).arePlayersVisible();
    }

    public boolean canStack(String uuid) {
        if (getPreferences(uuid) == null) {
            return true;
        } else return getPreferences(uuid).canStack();
    }

    public int getTime(String uuid) {
        if (getPreferences(uuid) == null) {
            return 0;
        } else return getPreferences(uuid).getTime();
    }

    public boolean isClear(String uuid) {
        if (getPreferences(uuid) == null) {
            return true;
        } else return getPreferences(uuid).isClear();
    }

    public void setPlayersVisible(String uuid, int bool) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement register = connection.prepareStatement("UPDATE Preferences SET PlayersVisible='" + bool + "' WHERE Uuid='" + uuid + "'");
            register.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not update the player." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public void setStacker(String uuid, int bool) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement register = connection.prepareStatement("UPDATE Preferences SET Stacker='" + bool + "' WHERE Uuid='" + uuid + "'");
            register.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not update the player." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public void setTime(String uuid, int time) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement register = connection.prepareStatement("UPDATE Preferences SET Time='" + time + "' WHERE Uuid='" + uuid + "'");
            register.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not update the player." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public void setClear(String uuid, int bool) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement register = connection.prepareStatement("UPDATE Preferences SET Weather='" + bool + "' WHERE Uuid='" + uuid + "'");
            register.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not update the player." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public void togglePlayersVisible(String uuid) {
        Connection connection = connect.getConnection();
        PlayerPreference preferences = getPreferences(uuid);
        int bool = (preferences.arePlayersVisible()) ? 0 : 1;
        try {
            PreparedStatement register = connection.prepareStatement("UPDATE Preferences SET PlayersVisible='" + bool + "' WHERE Uuid='" + uuid + "'");
            register.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not update the player." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public void toggleStacker(String uuid) {
        Connection connection = connect.getConnection();
        PlayerPreference preferences = getPreferences(uuid);
        int bool = (preferences.canStack()) ? 0 : 1;
        try {
            PreparedStatement register = connection.prepareStatement("UPDATE Preferences SET Stacker='" + bool + "' WHERE Uuid='" + uuid + "'");
            register.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not update the player." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public void toggleTime(String uuid) {
        Connection connection = connect.getConnection();
        PlayerPreference preferences = getPreferences(uuid);
        int time = (preferences.getTime() == 3) ? 0 : preferences.getTime() + 1;
        try {
            PreparedStatement register = connection.prepareStatement("UPDATE Preferences SET Time='" + time + "' WHERE Uuid='" + uuid + "'");
            register.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not update the player." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public void toggleWeather(String uuid) {
        Connection connection = connect.getConnection();
        PlayerPreference preferences = getPreferences(uuid);
        int bool = (preferences.isClear()) ? 0 : 1;
        try {
            PreparedStatement register = connection.prepareStatement("UPDATE Preferences SET Weather='" + bool + "' WHERE Uuid='" + uuid + "'");
            register.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not update the player." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }
}
