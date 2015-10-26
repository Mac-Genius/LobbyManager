package io.github.mac_genius.lobbymanager.database;

import io.github.mac_genius.lobbymanager.Parkour.ParkourCourse;
import io.github.mac_genius.lobbymanager.ServerSettings;
import org.bukkit.Location;
import org.fusesource.jansi.Ansi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Mac on 10/25/2015.
 */
public class ParkourCourses {
    private ServerSettings settings;
    private SQLConnect connect;

    public ParkourCourses(ServerSettings settings) {
        this.settings = settings;
        connect = new SQLConnect(settings);
    }

    public void addCourse(String name, int checkpoints, Location start, Location finish) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement fetch = connection.prepareStatement("SELECT * FROM ParkourCourses WHERE Name='" + name + "'");
            ResultSet results = fetch.executeQuery();
            String fetchedname = "";
            while (results.next()) {
                fetchedname = results.getString(2);
            }
            if (fetchedname.equals("")) {
                addToTable(name, checkpoints, start, finish);
            }
            connection.close();
        } catch (SQLException e) {
            addToTable(name, checkpoints, start, finish);
        }
    }

    private void addToTable(String name, int checkpoints, Location start, Location finish) {
        Connection connection = connect.getConnection();
        try {
            PreparedStatement add = connection.prepareStatement("INSERT INTO ParkourCourses(Name, Checkpoints, StartX, StartY, StartZ, FinishX, FinishY, FinishZ) VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
            add.setString(1, name);
            add.setInt(2, checkpoints);
            add.setDouble(3, start.getX());
            add.setDouble(4, start.getY());
            add.setDouble(5, start.getZ());
            add.setDouble(6, finish.getX());
            add.setDouble(7, start.getX());
            add.setDouble(8, start.getX());
            add.executeUpdate();
            connection.close();
        } catch (SQLException c) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not add the parkour course." + Ansi.ansi().fg(Ansi.Color.WHITE));
        }
    }

    public ArrayList<ParkourCourse> getCourses() {
        Connection connection = connect.getConnection();
        ArrayList<ParkourCourse> courses = new ArrayList<>();
        try {
            PreparedStatement fetch = connection.prepareStatement("SELECT * FROM ParkourCourses");
            ResultSet results = fetch.executeQuery();
            while (results.next()) {
                Location start = new Location(settings.getPlugin().getServer().getWorld("world"), results.getDouble(5), results.getDouble(6), results.getDouble(7));
                Location finish = new Location(settings.getPlugin().getServer().getWorld("world"), results.getDouble(8), results.getDouble(9), results.getDouble(10));
                ParkourCourse course = new ParkourCourse(results.getString(2), results.getInt(3), results.getString(4), start, finish);
                courses.add(course);
                settings.getPlugin().getLogger().info("Loaded parkour course \"" + results.getString(2) + "\" with " + results.getInt(3) + " checkpoints.");
                settings.getPlugin().getLogger().info("The start is at " + results.getDouble(5) + " " + results.getDouble(6)
                        + " " + results.getDouble(7)
                        + " and the finish is at " + results.getDouble(8)
                        + " " + results.getDouble(9) + " "
                        + results.getDouble(10) + ".");
            }
            connection.close();
            return courses;
        } catch (SQLException e) {
            settings.getPlugin().getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED) + "Could not fetch parkour courses." + Ansi.ansi().fg(Ansi.Color.WHITE));
            return courses;
        }
    }
}
