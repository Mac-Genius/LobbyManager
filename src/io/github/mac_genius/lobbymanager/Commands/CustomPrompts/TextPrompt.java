package io.github.mac_genius.lobbymanager.Commands.CustomPrompts;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

/**
 * Created by Mac on 10/26/2015.
 */
public class TextPrompt extends StringPrompt {
    private HoloText text;

    public TextPrompt(HoloText text) {
        this.text = text;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return ChatColor.LIGHT_PURPLE + "[HoloText] " + ChatColor.WHITE + "Please type message " + (text.getLines().size() + 1) + ".";
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {
        text.getLines().add(ChatColor.translateAlternateColorCodes('&', s));
        if (text.getLines().size() != text.getAmount()) {
            return new TextPrompt(text);
        } else return new EndPrompt(text);
    }
}
