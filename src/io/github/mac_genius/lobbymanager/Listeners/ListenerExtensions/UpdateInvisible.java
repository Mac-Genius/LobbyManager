package io.github.mac_genius.lobbymanager.Listeners.ListenerExtensions;

import io.github.mac_genius.lobbymanager.ServerSettings;
import io.github.mac_genius.lobbymanager.database.Preferences;
import io.github.mac_genius.lobbymanager.database.SQLObjects.PlayerPreference;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

/**
 * Created by Mac on 10/22/2015.
 */
public class UpdateInvisible {
    private ServerSettings settings;
    private Player player;

    public UpdateInvisible(ServerSettings settings, Player player) {
        this.settings = settings;
        this.player = player;
        update();
    }

    private void update() {
        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (Player p : players) {
            PlayerPreference preference = new Preferences(settings).getPreferences(p.getUniqueId().toString());
            if (!preference.arePlayersVisible()) {
                p.hidePlayer(player);
            }
        }
    }
}
