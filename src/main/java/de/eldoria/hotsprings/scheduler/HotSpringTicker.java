package de.eldoria.hotsprings.scheduler;

import de.eldoria.eldoutilities.core.EldoUtilities;
import de.eldoria.eldoutilities.localization.Replacement;
import de.eldoria.eldoutilities.messages.MessageChannel;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.messages.MessageType;
import de.eldoria.eldoutilities.messages.channeldata.ChannelData;
import de.eldoria.hotsprings.HotSprings;
import de.eldoria.hotsprings.config.Configuration;
import de.eldoria.hotsprings.config.HotSpring;
import de.eldoria.hotsprings.config.Limit;
import de.eldoria.hotsprings.config.LimitType;
import de.eldoria.hotsprings.config.Limits;
import de.eldoria.hotsprings.config.SpringSettings;
import de.eldoria.hotsprings.worldguard.HotSpringRegister;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.w3c.dom.CharacterData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HotSpringTicker extends BukkitRunnable {
    private final HotSpringRegister register;
    private final Economy economy;
    private final Configuration configuration;
    private final Limits limits;
    private final MessageSender sender;

    public HotSpringTicker(HotSpringRegister register, Economy economy, Configuration configuration, Limits limits) {
        this.register = register;
        this.economy = economy;
        this.configuration = configuration;
        this.limits = limits;
        sender = MessageSender.getPluginMessageSender(HotSprings.class);
    }

    @Override
    public void run() {
        MessageChannel<? extends ChannelData> messageChannel = configuration.getSettings().getMessageMode();

        for (Map.Entry<Player, HotSpring> springEntry : register.getActiveHotSprings().entrySet()) {
            Player player = springEntry.getKey();
            HotSpring hotSpring = springEntry.getValue();
            SpringSettings springSettings = configuration.getSpringSettings();

            Material type = player.getLocation().getBlock().getType();

            if (hotSpring.isRequireLava() && hotSpring.isRequireWater()) {
                if (type != Material.LAVA && type != Material.WATER) continue;
            } else {
                if (hotSpring.isRequireWater() && type != Material.WATER ) continue;
                if (hotSpring.isRequireLava() && type != Material.LAVA) continue;
            }


            Limit limit = hotSpring.getLimit(player);
            Limit playerLimit = limits.applyLimit(player, limit);

            List<String> messages = new ArrayList<>();
            List<Replacement> replacements = new ArrayList<>();

            if (playerLimit.canReceive(player, LimitType.INTERVAL, springSettings)) {
                ConsoleCommandSender sender = Bukkit.getConsoleSender();
                for (String command : hotSpring.getCommands()) {
                    String replace = command.replace("%player%", player.getName());
                    Bukkit.dispatchCommand(sender, replace);
                }
                if (!hotSpring.getCommands().isEmpty()) {
                    messages.add("$granted.interval$");
                }
            } else {
                messages.add("$limit.interval$");
            }

            replacements.add(Replacement.create("PLAYER", player));
            if (playerLimit.canReceive(player, LimitType.MONEY, springSettings) && economy != null) {
                economy.depositPlayer(player, limit.getMoneyLimit());
                messages.add("$granted.money$");
                replacements.add(Replacement.create("MONEY", economy.format(limit.getMoneyLimit()), 'b'));
            } else if (economy != null) {
                messages.add("$limit.money$");
            }


            if (playerLimit.canReceive(player, LimitType.EXPERIENCE, springSettings)) {
                player.giveExp(limit.getExpLimit());
                messages.add("$granted.experience$");
                replacements.add(Replacement.create("EXPERIENCE", limit.getExpLimit(), 'b'));
            } else {
                messages.add("$limit.experience$");
            }

            Replacement[] repl = replacements.toArray(new Replacement[0]);
            for (int i = 0; i < messages.size(); i++) {
                String message = messages.get(i);
                EldoUtilities.getDelayedActions().schedule(() -> {
                    sender.sendLocalized(messageChannel, MessageType.NORMAL, player,
                            message, repl);
                }, i * 20 * 5);
            }

            player.playSound(player.getLocation(), configuration.getSettings().getRecieveSound(), SoundCategory.AMBIENT, 1, 1);
        }

        configuration.save();
    }
}
