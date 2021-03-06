package de.eldoria.hotsprings.worldguard;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import de.eldoria.eldoutilities.localization.ILocalizer;
import de.eldoria.eldoutilities.localization.Replacement;
import de.eldoria.hotsprings.HotSprings;
import de.eldoria.hotsprings.config.Configuration;
import org.jetbrains.annotations.Nullable;

public class HotSpringFlag extends Flag<String> {
    private final Configuration configuration;
    private ILocalizer localizer = null;

    public HotSpringFlag(Configuration configuration) {
        super("hotspring");
        this.configuration = configuration;
    }

    @Override
    public String parseInput(FlagContext context) throws InvalidFlagFormat {
        if (!configuration.hasHotSpring(context.getUserInput())) {
            String names = configuration.getHotSpringNames();
            throw new InvalidFlagFormat(loc().getMessage("error.invalidSpring", Replacement.create("VALUES", names)));
        }
        return context.getUserInput();
    }

    private ILocalizer loc() {
        if (localizer == null) {
            localizer = ILocalizer.getPluginLocalizer(HotSprings.class);
        }
        return localizer;
    }

    @Override
    public String unmarshal(@Nullable Object o) {
        if (o == null) return null;
        String name = (String) o;
        if (configuration.hasHotSpring(name)) {
            return name;
        }
        return "default";
    }

    @Override
    public Object marshal(String o) {
        return o;
    }

    @Override
    public String getDefault() {
        return null;
    }


}
