package de.eldoria.hotsprings.config;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.eldoutilities.serialization.TypeResolvingMap;
import de.eldoria.eldoutilities.utils.PermUtil;
import de.eldoria.hotsprings.util.Permissions;
import lombok.Data;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
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
        expLimit = map.getValueOrDefault("expLimit", expLimit);
        moneyLimit = map.getValueOrDefault("moneyLimit", moneyLimit);
        intervalLimit = map.getValueOrDefault("intervalLimit", intervalLimit);
    }

    public void apply(Limit limit) {
        expLimit += limit.expLimit;
        moneyLimit += limit.moneyLimit;
        intervalLimit += limit.intervalLimit;
    }

    public boolean canReceive(Player player, LimitType limitType, SpringSettings settings) {
        int multi = 0;
        switch (limitType) {
            case EXPERIENCE:
                multi = PermUtil.findHighestIntPermission(player, Permissions.EXPERIENCE_LIMIT_MULTI);
                return expLimit <= settings.getMaxExperience() * multi;
            case MONEY:
                multi = PermUtil.findHighestIntPermission(player, Permissions.MONEY_LIMIT_MULTI);
                return moneyLimit <= settings.getMaxMoney();
            case INTERVAL:
                multi = PermUtil.findHighestIntPermission(player, Permissions.INTERVAL_LIMIT_MULTI);
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
