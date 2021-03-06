package de.eldoria.hotsprings.config;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import lombok.Data;
import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SerializableAs("hotSpringSpringSettings")
@Data
public class SpringSettings implements ConfigurationSerializable {
    private int maxMoney = 5000;
    private int maxExperience = 2000;
    private int maxIntervals = 1000;
    private int interval = 120;

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
