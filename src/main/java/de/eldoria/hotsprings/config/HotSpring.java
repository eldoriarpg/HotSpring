package de.eldoria.hotsprings.config;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.eldoutilities.serialization.TypeResolvingMap;
import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
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

    public Limit getLimit() {
        return new Limit(experience, money);
    }
}
