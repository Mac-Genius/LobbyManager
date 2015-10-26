package io.github.mac_genius.lobbymanager.Commands;

import io.github.mac_genius.lobbymanager.Commands.CustomPrompts.HoloText;
import io.github.mac_genius.lobbymanager.Commands.CustomPrompts.SizePrompt;
import io.github.mac_genius.lobbymanager.ServerSettings;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Created by Mac on 10/25/2015.
 */
public class CreateText implements CommandExecutor {
    private ServerSettings settings;

    public CreateText(ServerSettings settings) {
        this.settings = settings;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("holotext")) {
            if (commandSender instanceof Player) {
                HoloText text = new HoloText((Player)commandSender);
                ConversationFactory factory = new ConversationFactory(settings.getPlugin());
                Conversation conversation = factory.withFirstPrompt(new SizePrompt(text)).withLocalEcho(true).buildConversation((Player) commandSender);
                conversation.begin();
            }
            return true;
        }
        return false;
    }
}
