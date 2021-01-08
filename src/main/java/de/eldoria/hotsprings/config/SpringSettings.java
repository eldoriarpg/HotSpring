package de.eldoria.hotsprings.config;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.eldoutilities.serialization.TypeResolvingMap;
import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SerializableAs("hotSpringSpringSettings")
@Getter
public class SpringSettings implements ConfigurationSerializable {
    private int maxMoney = 5000;
    private int maxExperience = 2000;
    private int maxIntervals = 1000;
    private int interval = 120;

    public SpringSettings() {
    }

    public SpringSettings(Map<String, Object> objectMap) {
        TypeResolvingMap map = SerializationUtil.mapOf(objectMap);
        maxMoney = map.getValueOrDefault("maxMoney", maxMoney);
        maxExperience = map.getValueOrDefault("maxExperience", maxExperience);
        maxIntervals = map.getValueOrDefault("maxReceivals", maxIntervals);
        interval = map.getValueOrDefault("interval", interval);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("maxMoney", maxMoney)
                .add("maxExperience", maxExperience)
                .add("maxIntervals", maxIntervals)
                .add("interval", interval)
                .build();
    }
}
