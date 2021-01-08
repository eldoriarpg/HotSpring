package de.eldoria.hotsprings.config;

import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class Limits {
    private int day;
    private Map<UUID, Limit> limits;

    public Limit applyLimit(Player player, Limit limit) {
        int dayOfWeek = LocalDateTime.now().getDayOfWeek().getValue();
        if (dayOfWeek != day) {
            limits.clear();
            day = dayOfWeek;
        }
        Limit currLimit = limits.computeIfAbsent(player.getUniqueId(), Limit::new);
        currLimit.apply(limit);
        return currLimit;
    }
}
