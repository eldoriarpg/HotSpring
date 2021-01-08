package de.eldoria.hotsprings;

import de.eldoria.eldoutilities.localization.Replacement;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.hotsprings.config.Configuration;
import de.eldoria.hotsprings.config.HotSpring;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HotSpringRegister {
    private final Map<Player, HotSpring> hotSprings = new HashMap<>();
    private final Configuration configuration;
    private final Plugin plugin;
    private final MessageSender sender;

    public HotSpringRegister(Configuration configuration, Plugin plugin) {
        this.configuration = configuration;
        this.plugin = plugin;
        this.sender = MessageSender.getPluginMessageSender(HotSprings.class);
    }

    public Map<Player, HotSpring> getActiveHotSprings() {
        return Collections.unmodifiableMap(hotSprings);
    }

    public void registerPlayer(Player player, String springName) {
        if (configuration.hasHotSpring(springName)) {
            HotSpring spring = configuration.getHotSpring(springName);
            hotSprings.put(player, spring);
        } else {
            plugin.getLogger().info("Hot Spring with name " + springName + " is not defined. Using default.");
            hotSprings.put(player, HotSpring.DEFAULT);
        }
        sendMessage(player, "spring.enter");
    }

    public void unregisterPlayer(Player player) {
        hotSprings.remove(player);
        sendMessage(player, "spring.leave");
    }

    private void sendMessage(Player player, String message, Replacement... replacements) {
        switch (configuration.getSettings().getMessageMode()) {
            case TITLE:
                sender.sendLocalizedTitle(player, "2", message, "", 20, 50, 10, replacements);
                break;
            case SUBTITLE:
                sender.sendLocalizedTitle(player, "2", "", message, 20, 50, 10, replacements);
                break;
            case CHAT:
                sender.sendLocalizedMessage(player, message, replacements);
                break;
            case ACTION_BAR:
                sender.sendLocalizedActionBar(player, message, replacements);
                break;
        }
    }
}
