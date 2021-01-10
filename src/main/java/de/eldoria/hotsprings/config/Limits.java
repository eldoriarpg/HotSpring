package de.eldoria.hotsprings.config;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.eldoutilities.serialization.TypeResolvingMap;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("hotSpringLimits")
public class Limits implements ConfigurationSerializable {
    private final Map<UUID, Limit> limits = new HashMap<>();
    private int day = getCurrentDay();

    public Limits() {
    }

    public Limits(Map<String, Object> objectMap) {
        TypeResolvingMap map = SerializationUtil.mapOf(objectMap);
        day = map.getValueOrDefault("day", getCurrentDay());
        map.listToMap(limits, "limits", Limit::getUuid);
    }

    private static int getCurrentDay() {
        return LocalDateTime.now().getDayOfWeek().getValue();
    }

    public Limit applyLimit(Player player, Limit limit) {
        int dayOfWeek = getCurrentDay();
        if (dayOfWeek != day) {
            limits.clear();
            day = dayOfWeek;
        }
        Limit currLimit = limits.computeIfAbsent(player.getUniqueId(), Limit::new);
        currLimit.apply(limit);
        return currLimit;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("day", day)
                .add("limits", limits)
                .build();
    }
}
