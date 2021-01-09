package de.eldoria.hotsprings;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.hotsprings.config.Configuration;
import de.eldoria.hotsprings.listener.HotSpringFlagHandler;
import de.eldoria.hotsprings.scheduler.HotSpringTicker;
import de.eldoria.hotsprings.worldguard.HotSpringFlag;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HotSprings extends EldoPlugin {

    private Economy economy = null;
    private Configuration configuration;
    private boolean initialized = false;
    private HotSpringFlag hotSpringFlag;
    private HotSpringRegister hotSpringRegister;
    private HotSpringTicker hotSpringTicker;

    public static Logger logger() {
        return getInstance(HotSprings.class).getLogger();
    }

    @Override
    public void onLoad() {
        registerFlag();
    }

    @Override
    public void onEnable() {
        if (!initialized) {
            configuration = new Configuration(this);
            setupWorldGuard();
            setupEconomy();
            hotSpringTicker = new HotSpringTicker(hotSpringRegister, economy, configuration, configuration.getLimits());
            initialized = true;
        } else {
            hotSpringTicker.cancel();
            configuration.reload();
        }
        int interval = configuration.getSpringSettings().getInterval() * 20;
        hotSpringTicker.runTaskTimer(this, interval, interval);
    }

    public void registerFlag() {
        HotSpringFlag hotSpringFlag = new HotSpringFlag(configuration);
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            registry.register(hotSpringFlag);
        } catch (FlagConflictException e) {
            // some other plugin registered a flag by the same name already.
            // you can use the existing flag, but this may cause conflicts - be sure to check type
            getLogger().log(Level.SEVERE, "Could not register flag. Flag Conflict.", e);
            getPluginManager().disablePlugin(this);
        }
        getLogger().info("World Guard Flag registered.");
    }

    public void setupWorldGuard() {
        hotSpringRegister = new HotSpringRegister(configuration, this);
        WorldGuard.getInstance().getPlatform().getSessionManager()
                .registerHandler(new HotSpringFlagHandler.Factory(hotSpringFlag, hotSpringRegister), null);
        getLogger().info("HotSpring Flag Handler initialized. Hook into World Guard successful.");
    }

    public boolean setupEconomy() {
        if (!getPluginManager().isPluginEnabled("Vault")) {
            getLogger().info("Vault is not installed. Economy Service not available.");
            return false;
        }
        RegisteredServiceProvider<Economy> economy = getServer().getServicesManager().getRegistration(Economy.class);

        if (economy == null) {
            getLogger().info("Failed to hook into vault. Economy Service not available.");
            return false;
        }
        this.economy = economy.getProvider();
        assert this.economy != null;
        getLogger().info("Hooked into vault successfully.");
        return true;
    }
}
