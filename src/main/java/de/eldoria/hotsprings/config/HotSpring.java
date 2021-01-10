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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@SerializableAs("hotSpringsHotSpring")
public class HotSpring implements ConfigurationSerializable {

    public static final HotSpring DEFAULT = new HotSpring("default");

    private final String name;
    private int money = 10;
    private int experience = 10;
    private List<String> commands = new ArrayList<>();
    private boolean requireWater = false;
    private boolean requireLava = false;

    public HotSpring(String name) {
        this.name = name;
    }

    public HotSpring(Map<String, Object> objectMap) {
        TypeResolvingMap map = SerializationUtil.mapOf(objectMap);
        name = map.getValue("name");
        money = map.getValueOrDefault("money", money);
        experience = map.getValueOrDefault("experience", experience);
        commands = map.getValueOrDefault("commands", commands);
        requireWater = map.getValueOrDefault("requireWater", requireWater);
        requireLava = map.getValueOrDefault("requireLava", requireLava);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("name", name)
                .add("money", money)
                .add("experience", experience)
                .add("commands", commands)
                .add("requireWater", requireWater)
                .add("requireLava", requireLava)
                .build();
    }

    public Limit getLimit(Player player) {
        int expMulti = PermUtil.findHighestIntPermission(player, Permissions.EXPERIENCE_MULTI);
        int moneyMulti = PermUtil.findHighestIntPermission(player, Permissions.MONEY_MULTI);
        return new Limit(experience * expMulti, money * moneyMulti);
    }
}
