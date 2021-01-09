package de.eldoria.hotsprings;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import de.eldoria.eldoutilities.localization.ILocalizer;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.hotsprings.commands.HotSpringCommand;
import de.eldoria.hotsprings.config.Configuration;
import de.eldoria.hotsprings.config.GeneralSettings;
import de.eldoria.hotsprings.config.HotSpring;
import de.eldoria.hotsprings.config.Limit;
import de.eldoria.hotsprings.config.Limits;
import de.eldoria.hotsprings.config.SpringSettings;
import de.eldoria.hotsprings.listener.HotSpringFlagHandler;
import de.eldoria.hotsprings.scheduler.HotSpringTicker;
import de.eldoria.hotsprings.worldguard.HotSpringFlag;
import de.eldoria.hotsprings.worldguard.HotSpringRegister;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
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
        registerConfig();
        configuration = new Configuration(this);
        registerFlag();
    }

    private void registerConfig() {
        ConfigurationSerialization.registerClass(GeneralSettings.class);
        ConfigurationSerialization.registerClass(HotSpring.class);
        ConfigurationSerialization.registerClass(Limit.class);
        ConfigurationSerialization.registerClass(Limits.class);
        ConfigurationSerialization.registerClass(SpringSettings.class);
    }

    @Override
    public void onEnable() {
        if (!initialized) {
            ILocalizer localizer = ILocalizer.create(this, "de_DE", "en_US");
            localizer.setLocale(configuration.getLanguage());
            MessageSender.create(this, "§6[§cH§3S§6] ", '2', 'c');
            setupWorldGuard();
            setupEconomy();
            registerCommand("hotsprings", new HotSpringCommand(this, configuration));
            initialized = true;
        } else {
            hotSpringTicker.cancel();
            configuration.reload();
        }
        hotSpringTicker = new HotSpringTicker(hotSpringRegister, economy, configuration, configuration.getLimits());
        int interval = configuration.getSpringSettings().getInterval() * 20;
        hotSpringTicker.runTaskTimer(this, interval, interval);
    }

    public void registerFlag() {
        hotSpringFlag = new HotSpringFlag(configuration);
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
