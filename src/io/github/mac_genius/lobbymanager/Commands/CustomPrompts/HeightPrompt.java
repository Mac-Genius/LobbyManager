package io.github.mac_genius.lobbymanager.Commands.CustomPrompts;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;

/**
 * Created by Mac on 10/26/2015.
 */
public class HeightPrompt extends NumericPrompt {
    private HoloText text;

    public HeightPrompt(HoloText text) {
        this.text = text;
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, Number number) {
        text.setHeight(number.doubleValue() + 2);
        return new TextPrompt(text);
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return ChatColor.LIGHT_PURPLE + "[HoloText] " + ChatColor.WHITE + "What should the height of the first message be relative to your head?";
    }
}
