package io.github.mac_genius.lobbymanager.Commands.Cloak;

import com.mojang.authlib.GameProfile;
import io.github.mac_genius.lobbymanager.ServerSettings;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftOfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Mac on 10/28/2015.
 */
public class CloakRun implements Runnable {
    private Player player;
    private String newName;
    private ServerSettings settings;

    public CloakRun(Player player, String newName, ServerSettings settings) {
        this.player = player;
        this.newName = newName;
        this.settings = settings;
    }

    @Override
    public void run() {
        changeSkin();
    }

    private void changeSkin() {
        GameProfile newProfile;
        if (settings.getProfiles().containsKey(newName)) {
            newProfile = settings.getProfiles().get(newName);
        } else {
            newProfile = new FetchProfile(newName, player).getProfile();
            settings.getProfiles().put(newProfile.getName(), newProfile);
        }
        EntityPlayer onlinePlayer = ((CraftPlayer)player).getHandle();
        if (!settings.getCloaked().containsKey(onlinePlayer.getUniqueID())) {
            settings.getCloaked().put(onlinePlayer.getUniqueID(), player.getName());
            settings.getOldName().put(onlinePlayer.getUniqueID(), player.getDisplayName());
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, onlinePlayer));
        }

        try {
            // Gets the gameprofile data
            Class<?> packet = onlinePlayer.getProfile().getClass();
            Field name = packet.getDeclaredField("name");
            name.setAccessible(true);
            name.set(onlinePlayer.getProfile(), newProfile.getName());

            // sets the properties
            Field properties = packet.getDeclaredField("properties");
            properties.setAccessible(true);
            properties.set(onlinePlayer.getProfile(), newProfile.getProperties());

            /*// sets the id
            Field uuid = packet.getDeclaredField("id");
            uuid.setAccessible(true);
            uuid.set(onlinePlayer.getProfile(), newProfile.getId());*/

        } catch (NoSuchFieldException | IllegalAccessException e) {
            player.sendMessage(ChatColor.DARK_PURPLE + "[Cloak] " + ChatColor.WHITE + "You couldn't be cloaked!");
            e.printStackTrace();
        }
        // Packets to clients
        for (Player p : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, onlinePlayer));
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p != player) {
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(onlinePlayer.getId()));
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(onlinePlayer));
            }
        }

        player.sendMessage(ChatColor.DARK_PURPLE + "[Cloak] " + ChatColor.WHITE + "You have been cloaked!");
        player.setDisplayName(newProfile.getName());
        Location location = player.getLocation();
        settings.getPlugin().getServer().getScheduler().runTask(settings.getPlugin(), new updateSurrounding(location));
    }

    private class updateSurrounding implements Runnable {
        private Location location;

        public updateSurrounding(Location location) {
            this.location = location;
        }

        @Override
        public void run() {
            for (World w : Bukkit.getServer().getWorlds()) {
                if (w != player.getWorld()) {
                    player.teleport(w.getSpawnLocation());
                    break;
                }
            }
            player.teleport(location);
        }
    }
}
