package io.github.mac_genius.lobbymanager.Commands.CustomPrompts;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;

/**
 * Created by Mac on 10/26/2015.
 */
public class SizePrompt extends NumericPrompt {
    private HoloText text;

    public SizePrompt(HoloText text) {
        this.text = text;
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, Number number) {
        text.setAmount(number.intValue());
        return new HeightPrompt(text);
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return ChatColor.LIGHT_PURPLE + "[HoloText] " + ChatColor.WHITE + "How many lines should the text be?";
    }
}
