package io.github.mac_genius.lobbymanager.Listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.github.mac_genius.lobbymanager.LobbyManager;
import io.github.mac_genius.lobbymanager.Util.ServerInfo;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 * Created by Mac on 3/21/2016.
 */
public class ServerSelectListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("ServerList")) {
            int size = in.readInt();
            for (int i = 0; i < size; i++) {
                ServerInfo info = new ServerInfo(in.readUTF(), in.readBoolean(), in.readBoolean(), in.readInt(), in.readInt());
                /*LobbyManager.getSingleton().getLogger().info(info.getName());
                LobbyManager.getSingleton().getLogger().info(info.isOnline() + "");
                LobbyManager.getSingleton().getLogger().info(info.isCanConnect() + "");
                LobbyManager.getSingleton().getLogger().info(info.getPlayers() + "");
                LobbyManager.getSingleton().getLogger().info(info.getMaxPlayers() + "");*/
                LobbyManager.getSingleton().getServerSelectManager().updateServer(info);
            }
        }
    }
}
