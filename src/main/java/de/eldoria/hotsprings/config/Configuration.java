package de.eldoria.hotsprings.config;

import de.eldoria.eldoutilities.configuration.EldoConfig;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration extends EldoConfig {
    @Getter
    private GeneralSettings settings = new GeneralSettings();
    private final Map<String, HotSpring> springs = new HashMap<>();
    @Getter
    private SpringSettings springSettings = new SpringSettings();
    private FileConfiguration limitsFile;
    @Getter
    private Limits limits;

    public Configuration(Plugin plugin) {
        super(plugin);
    }

    @Override
    protected void init() {
        getLimitsFile();
    }

    @Override
    protected void saveConfigs() {
        getConfig().set("springs", new ArrayList<>(springs.values()));
        getConfig().set("springSettings", springSettings);
        getConfig().set("settings", settings);
        getLimitsFile().set("limits", limits);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void reloadConfigs() {
        springs.clear();
        List<HotSpring> springs = (List<HotSpring>) getConfig().getList("springs", new ArrayList<HotSpring>());
        assert springs != null;
        springs.forEach(s -> this.springs.put(s.getName().toLowerCase(), s));
        settings = getConfig().getObject("settings", GeneralSettings.class);
        springSettings = getConfig().getObject("springSettings", SpringSettings.class);
        limits = getLimitsFile().getObject("limits", Limits.class);
    }

    public HotSpring getHotSpring(String name) {
        return springs.getOrDefault(name.toLowerCase(), HotSpring.DEFAULT);
    }

    public boolean hasHotSpring(String name) {
        return springs.containsKey(name.toLowerCase());
    }

    private FileConfiguration getLimitsFile() {
        return loadConfig("limits", f -> f.set("limits", new Limits()), false);
    }
}
