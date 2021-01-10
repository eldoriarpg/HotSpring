package de.eldoria.hotsprings.config;

import de.eldoria.eldoutilities.configuration.EldoConfig;
import de.eldoria.hotsprings.HotSprings;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Configuration extends EldoConfig {
    @Getter
    private boolean updateCheck;
    @Getter
    private GeneralSettings settings;
    private Map<String, HotSpring> springTypes;
    @Getter
    private SpringSettings springSettings;
    @Getter
    private Limits limits;
    @Getter
    private String language;

    public Configuration(Plugin plugin) {
        super(plugin);
    }

    @Override
    protected void init() {
        getLimitsFile();
        settings = new GeneralSettings();
        springTypes = new HashMap<>();
        springSettings = new SpringSettings();
        limits = new Limits();
    }

    @Override
    protected void saveConfigs() {
        getConfig().set("updateCheck", updateCheck);
        getConfig().set("language", language);
        getConfig().set("springTypes", new ArrayList<>(springTypes.values()));
        getConfig().set("springSettings", springSettings);
        getConfig().set("settings", settings);
        getLimitsFile().set("limits", limits);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void reloadConfigs() {
        springTypes.clear();
        List<HotSpring> springTypes = (List<HotSpring>) getConfig().get("springTypes", new ArrayList<HotSpring>());
        assert springTypes != null;
        springTypes.forEach(this::registerHotSpring);
        if (this.springTypes.isEmpty()) {
            registerHotSpring(HotSpring.DEFAULT);
            HotSprings.logger().info("§2No Hot Springs settings defined. Creating default.");
        }
        settings = getConfig().getObject("settings", GeneralSettings.class, new GeneralSettings());
        springSettings = getConfig().getObject("springSettings", SpringSettings.class, new SpringSettings());
        limits = getLimitsFile().getObject("limits", Limits.class, new Limits());
        language = getConfig().getString("language", "en_US");
        updateCheck = getConfig().getBoolean("updateCheck", true);
    }

    public void registerHotSpring(HotSpring spring) {
        springTypes.put(spring.getName().toLowerCase(), spring);
    }

    public void unregisterHotSpring(HotSpring spring) {
        springTypes.remove(spring.getName().toLowerCase());
    }

    public HotSpring getHotSpring(String name) {
        return springTypes.getOrDefault(name.toLowerCase(), HotSpring.DEFAULT);
    }

    public boolean hasHotSpring(String name) {
        return springTypes.containsKey(name.toLowerCase());
    }

    private FileConfiguration getLimitsFile() {
        return loadConfig("limits", f -> f.set("limits", new Limits()), false);
    }

    public Collection<HotSpring> getHotSprings() {
        return springTypes.values();
    }

    public String getHotSpringNames() {
        return getHotSprings().stream().map(HotSpring::getName).collect(Collectors.joining(", "));
    }
}
