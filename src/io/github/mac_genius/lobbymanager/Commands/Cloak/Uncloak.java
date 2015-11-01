package io.github.mac_genius.lobbymanager.Commands.Cloak;

import com.mojang.authlib.GameProfile;
import io.github.mac_genius.lobbymanager.ServerSettings;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

/**
 * Created by Mac on 10/28/2015.
 */
public class Uncloak implements CommandExecutor {
    private ServerSettings settings;

    public Uncloak(ServerSettings settings) {
        this.settings = settings;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("uncloak")) {
            if (commandSender instanceof Player && commandSender.hasPermission("lobbymanager.cloak")) {
                Player player = (Player) commandSender;
                settings.getPlugin().getServer().getScheduler().runTaskAsynchronously(settings.getPlugin(), new Decloak(player));
                return true;
            }
        }
        return false;
    }

    private class Decloak implements Runnable {
        private Player player;

        public Decloak(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            GameProfile newProfile;
            if (settings.getProfiles().containsKey(settings.getCloaked().get(player.getUniqueId()))) {
                newProfile = settings.getProfiles().get(settings.getCloaked().get(player.getUniqueId()));
            } else {
                newProfile = new FetchProfile(settings.getCloaked().get(player.getUniqueId()), player).getProfile();
                settings.getProfiles().put(newProfile.getName(), newProfile);
            }
            EntityPlayer onlinePlayer = ((CraftPlayer)player).getHandle();

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

            player.sendMessage(ChatColor.DARK_PURPLE + "[Cloak] " + ChatColor.WHITE + "You have been uncloaked!");
            player.setDisplayName(settings.getOldName().get(player.getUniqueId()));
            settings.getOldName().remove(player.getUniqueId());
            settings.getCloaked().remove(player.getUniqueId());
            Location location = player.getLocation();
            settings.getPlugin().getServer().getScheduler().runTask(settings.getPlugin(), new updateSurrounding(location, player));
        }
    }

    private class updateSurrounding implements Runnable {
        private Location location;
        private Player player;

        public updateSurrounding(Location location, Player player) {
            this.location = location;
            this.player = player;
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
