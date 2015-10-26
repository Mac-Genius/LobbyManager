package io.github.mac_genius.lobbymanager.Commands.CustomPrompts;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * Created by Mac on 10/26/2015.
 */
public class EndPrompt extends MessagePrompt {
    private HoloText text;

    public EndPrompt(HoloText text) {
        this.text = text;
    }

    @Override
    protected Prompt getNextPrompt(ConversationContext conversationContext) {
        return null;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        Location location = text.getPlayer().getLocation();
        location.setY(location.getY() + text.getHeight());

        for (String g : text.getLines()) {
            Entity entity = text.getPlayer().getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
            ((ArmorStand)entity).setVisible(false);
            ((ArmorStand)entity).setGravity(false);
            ((ArmorStand)entity).setBasePlate(false);
            ((ArmorStand)entity).setCustomName(g);
            ((ArmorStand)entity).setCustomNameVisible(true);
            location.setY(location.getY() - .3);
        }
        return ChatColor.LIGHT_PURPLE + "[HoloText] " + ChatColor.WHITE + "Excellent! I will create your display for you.";
    }
}
