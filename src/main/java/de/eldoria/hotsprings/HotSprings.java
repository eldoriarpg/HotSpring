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

public class HotSprings extends EldoPlugin {

    private Economy economy = null;
    private Configuration configuration;
    private boolean initialized = false;
    private HotSpringFlag hotSpringFlag;
    private HotSpringRegister hotSpringRegister;
    private HotSpringTicker hotSpringTicker;

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
            initialized = true;
            hotSpringTicker = new HotSpringTicker(hotSpringRegister, economy, configuration, configuration.getLimits());
            int interval = configuration.getSpringSettings().getInterval() * 20;
            hotSpringTicker.runTaskTimer(this, interval, interval);
        } else {
            int interval = configuration.getSpringSettings().getInterval() * 20;
            hotSpringTicker.cancel();
            hotSpringTicker.runTaskTimer(this, interval, interval);
            configuration.reload();
        }

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
    }

    public void setupWorldGuard() {
        hotSpringRegister = new HotSpringRegister(configuration, this);
        WorldGuard.getInstance().getPlatform().getSessionManager()
                .registerHandler(new HotSpringFlagHandler.Factory(hotSpringFlag, hotSpringRegister), null);
    }

    public boolean setupEconomy() {
        if (!getPluginManager().isPluginEnabled("Vault")) {
            return false;
        }
        RegisteredServiceProvider<Economy> economy = getServer().getServicesManager().getRegistration(Economy.class);

        if (economy == null) {
            return false;
        }
        this.economy = economy.getProvider();
        assert this.economy != null;
        return true;
    }
}
