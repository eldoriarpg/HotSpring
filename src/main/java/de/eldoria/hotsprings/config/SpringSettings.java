package de.eldoria.hotsprings.config;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SerializableAs("hotSpringSpringSettings")
@Getter
public class SpringSettings implements ConfigurationSerializable {
    private final int maxMoney = 5000;
    private final int maxExperience = 2000;
    private final int maxIntervals = 1000;
    private final int interval = 120;

    public SpringSettings() {
    }

    public SpringSettings(Map<String, Object> objectMap) {
        SerializationUtil.mapOnObject(objectMap, this);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SerializationUtil.objectToMap(this);
    }
}
