package io.github.mac_genius.lobbymanager.Commands;

import io.github.mac_genius.lobbymanager.NPCHandler.MessageConfig;
import io.github.mac_genius.lobbymanager.database.NPCList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mac on 5/8/2015.
 */
public class Commands implements CommandExecutor {
    private Plugin plugin;
    private MessageConfig messageConfig;
    private HashMap<Entity, String> npcs;
    private HashMap<Entity, ArrayList<ArmorStand>> npcTags;

    public Commands(Plugin pluginin, MessageConfig messageConfig, HashMap<Entity, String> npcs, HashMap<Entity, ArrayList<ArmorStand>> npcTags) {
        plugin = pluginin;
        this.messageConfig = messageConfig;
        this.npcs = npcs;
        this.npcTags = npcTags;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (command.getName().equalsIgnoreCase("sm")) {
                if (strings.length == 0 && commandSender.hasPermission("sm.help")) {
                    commandSender.sendMessage(ChatColor.GREEN + "---------- LobbyManager Help ----------");
                    commandSender.sendMessage(ChatColor.GOLD + "/sm reload" + ChatColor.WHITE + " reloads the config");
                    commandSender.sendMessage(ChatColor.GOLD + "/sm help" + ChatColor.WHITE + " commands");
                    commandSender.sendMessage(ChatColor.GOLD + "/sm npcs VILLAGER <type> [job] [name]" + ChatColor.WHITE + " Spawns a villager.");
                    commandSender.sendMessage(ChatColor.GOLD + "/sm npcs <mob> [job] [name]" + ChatColor.WHITE + " Spawns a npc.");
                    commandSender.sendMessage(ChatColor.GOLD + "/sm npcd" + ChatColor.WHITE + " Deletes the entity you are facing.");
                    commandSender.sendMessage(ChatColor.GOLD + "/sm npcj [update/fetch] <job>" + ChatColor.WHITE + " Updates a npc's job or fetches their job.");
                    return true;
                }
                if (strings[0].equalsIgnoreCase("reload") && commandSender.hasPermission("sm.reload")) {
                    plugin.reloadConfig();
                    messageConfig.reloadConfig();
                    commandSender.sendMessage(ChatColor.GREEN + "[LobbyManager] " + ChatColor.WHITE + "Config reloaded!");
                    return true;
                }
                if (strings[0].equalsIgnoreCase("npcs")) {
                    if (strings[1] != null) {
                        Entity entity = null;
                        ArmorStand title;
                        ArmorStand rightClick;
                        String job = "";
                        ArrayList<ArmorStand> stands = new ArrayList<>();
                        if (strings[1].equalsIgnoreCase("villager")) {
                            String prof = "";
                            if (strings[2].equalsIgnoreCase("")) {
                                player.sendMessage(ChatColor.GREEN + "Please enter a profession.");
                            } else {
                                prof = strings[2];
                            }
                            if (strings[3].equalsIgnoreCase("")) {
                                player.sendMessage(ChatColor.GREEN + "Please enter its job.");
                                return true;
                            } else {
                                job = strings[3];
                            }
                            entity = player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
                            try {
                                ((Villager) entity).setProfession(Villager.Profession.valueOf(prof.toUpperCase()));
                            } catch (IllegalArgumentException e) {
                                player.sendMessage(ChatColor.RED + "Please add a correct profession.");
                                entity.remove();
                                return true;
                            }
                            String name = "";
                            int i = 4;
                            while (i < strings.length) {
                                name += strings[i] + " ";
                                i++;
                            }
                            Location playerLocation = player.getLocation();
                            playerLocation.setY(playerLocation.getY() + 1.25);
                            title = (ArmorStand) player.getWorld().spawnEntity(playerLocation, EntityType.ARMOR_STAND);
                            title.setGravity(false);
                            title.setSmall(true);
                            title.setVisible(false);
                            title.setCustomName(ChatColor.GREEN + name);
                            title.setCustomNameVisible(true);
                            playerLocation.setY(playerLocation.getY() - .35);
                            rightClick = (ArmorStand) player.getWorld().spawnEntity(playerLocation, EntityType.ARMOR_STAND);
                            rightClick.setGravity(false);
                            rightClick.setSmall(true);
                            rightClick.setVisible(false);
                            rightClick.setCustomName(ChatColor.GOLD + "" + ChatColor.BOLD + "Right Click");
                            rightClick.setCustomNameVisible(true);
                            stands.add(title);
                            stands.add(rightClick);
                        } else {
                            entity = player.getWorld().spawnEntity(player.getLocation(), EntityType.valueOf(strings[1].toUpperCase()));
                            String name = "";
                            if (strings[2].equalsIgnoreCase("")) {
                                player.sendMessage(ChatColor.GREEN + "Please enter its job.");
                                return true;
                            } else {
                                job = strings[2];
                            }
                            int i = 3;
                            while (i < strings.length) {
                                name += strings[i] + " ";
                                i++;
                            }
                            Location playerLocation = player.getLocation();
                            playerLocation.setY(playerLocation.getY() + 1.25);
                            title = (ArmorStand) player.getWorld().spawnEntity(playerLocation, EntityType.ARMOR_STAND);
                            title.setGravity(false);
                            title.setSmall(true);
                            title.setVisible(false);
                            title.setCustomName(ChatColor.GREEN + name);
                            title.setCustomNameVisible(true);
                            playerLocation.setY(playerLocation.getY() - .35);
                            rightClick = (ArmorStand) player.getWorld().spawnEntity(playerLocation, EntityType.ARMOR_STAND);
                            rightClick.setGravity(false);
                            rightClick.setSmall(true);
                            rightClick.setVisible(false);
                            rightClick.setCustomName(ChatColor.GOLD + "" + ChatColor.BOLD + "Right Click");
                            rightClick.setCustomNameVisible(true);
                            stands.add(title);
                            stands.add(rightClick);
                        }
                        NPCList list = new NPCList(plugin);
                        list.addNPC(entity, job);
                        npcs.put(entity, job);
                        npcTags.put(entity, stands);
                        if (entity instanceof LivingEntity) {
                            PotionEffect slowness = new PotionEffect(PotionEffectType.SLOW, 1000000000, 100, false, false);
                            ((LivingEntity) entity).addPotionEffect(slowness);
                        }
                    } else {
                        commandSender.sendMessage("Please enter a mob type.");
                    }
                    return true;
                }
                if (strings[0].equalsIgnoreCase("npcd")) {
                    NPCList list = new NPCList(plugin);
                    ArrayList<Entity> nearby = new ArrayList<>(player.getNearbyEntities(3, 3, 3));
                    for (Entity e : nearby) {
                        if (player.hasLineOfSight(e)) {
                            if (!(e instanceof ArmorStand)) {
                                for (ArmorStand armor : npcTags.get(e)) {
                                    armor.remove();
                                }
                                npcTags.remove(e);
                                list.removeNPC(e);
                                npcs.remove(e);
                                e.remove();
                                player.sendMessage(ChatColor.GREEN + "The entity has been successfully removed.");
                                break;
                            }
                        }
                    }
                    return true;
                }
                if (strings[0].equalsIgnoreCase("npcj")) {
                    String job = "";
                    if (strings[1].equalsIgnoreCase("update")) {
                        if (strings[2] != null && job.length() <= 20) {
                            job = strings[2];
                        } else {
                            player.sendMessage(ChatColor.RED + "Please type a job or make sure it is less than 20 characters.");
                            return true;
                        }
                        NPCList list = new NPCList(plugin);
                        ArrayList<Entity> nearby = new ArrayList<>(player.getNearbyEntities(3, 3, 3));
                        for (Entity e : nearby) {
                            if (player.hasLineOfSight(e)) {
                                if (!(e instanceof ArmorStand)) {
                                    list.setJob(e, job);
                                    npcs.put(e, job);
                                    player.sendMessage(ChatColor.GREEN + "The entity's job has been successfully updated.");
                                    break;
                                }
                            }
                        }
                    } else if (strings[1].equalsIgnoreCase("fetch")) {
                        NPCList list = new NPCList(plugin);
                        ArrayList<Entity> nearby = new ArrayList<>(player.getNearbyEntities(3, 3, 3));
                        for (Entity e : nearby) {
                            if (player.hasLineOfSight(e)) {
                                if (!(e instanceof ArmorStand)) {
                                    String jobTitle = list.getJob(e);
                                    player.sendMessage(ChatColor.GREEN + "The entity's job is \"" + ChatColor.WHITE + jobTitle + ChatColor.GREEN + "\".");
                                    break;
                                }
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
