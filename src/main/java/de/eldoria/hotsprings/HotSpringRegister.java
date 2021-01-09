package de.eldoria.hotsprings;

import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.messages.MessageType;
import de.eldoria.hotsprings.config.Configuration;
import de.eldoria.hotsprings.config.HotSpring;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HotSpringRegister {
    private final Map<Player, HotSpring> hotSprings = new HashMap<>();
    private final Plugin plugin;

    public HotSpringRegister(Configuration configuration, Plugin plugin) {
        this.plugin = plugin;
        this.configuration = configuration;
        this.sender = MessageSender.getPluginMessageSender(plugin);
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
        sender.sendLocalized(configuration.getSettings().getMessageMode(), MessageType.NORMAL, player,
                "spring.enter");
    }

    public void unregisterPlayer(Player player) {
        hotSprings.remove(player);
        sender.sendLocalized(configuration.getSettings().getMessageMode(), MessageType.NORMAL, player,
                "spring.leave");
    }

    protected final Configuration configuration;
    protected final MessageSender sender;
}
