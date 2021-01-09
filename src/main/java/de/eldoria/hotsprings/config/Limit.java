package de.eldoria.hotsprings.config;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.eldoutilities.serialization.TypeResolvingMap;
import lombok.Data;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

@Data
@SerializableAs("hotSpringsLimit")
public class Limit implements ConfigurationSerializable {
    private final UUID uuid;
    private int expLimit = 0;
    private int moneyLimit = 0;
    private int intervalLimit = 0;

    public Limit(UUID uniqueId) {
        uuid = uniqueId;
    }

    public Limit(int expLimit, int moneyLimit) {
        uuid = null;
        this.expLimit = expLimit;
        this.moneyLimit = moneyLimit;
        intervalLimit = 1;
    }

    public Limit(Map<String, Object> objectMap) {
        TypeResolvingMap map = SerializationUtil.mapOf(objectMap);
        uuid = map.getValueOrDefault("uuid", new UUID(0, 0));
        expLimit = map.getValueOrDefault("expLimit",expLimit);
        moneyLimit = map.getValueOrDefault("moneyLimit",moneyLimit);
        intervalLimit = map.getValueOrDefault("intervalLimit",intervalLimit);
    }

    public void apply(Limit limit) {
        expLimit += limit.expLimit;
        moneyLimit += limit.moneyLimit;
        intervalLimit += limit.intervalLimit;
    }

    public boolean canReceive(LimitType limitType, SpringSettings settings) {
        switch (limitType) {
            case EXPERIENCE:
                return expLimit <= settings.getMaxExperience();
            case MONEY:
                return moneyLimit <= settings.getMaxMoney();
            case INTERVAL:
                return intervalLimit <= settings.getMaxIntervals();
        }
        return true;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("uuid", uuid)
                .add("expLimit", expLimit)
                .add("moneyLimit", moneyLimit)
                .add("intervalLimit", intervalLimit)
                .build();
    }
}
