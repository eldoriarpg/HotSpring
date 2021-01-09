package de.eldoria.hotsprings.scheduler;

import de.eldoria.eldoutilities.localization.Replacement;
import de.eldoria.eldoutilities.messages.MessageChannel;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.messages.MessageType;
import de.eldoria.hotsprings.HotSpringRegister;
import de.eldoria.hotsprings.HotSprings;
import de.eldoria.hotsprings.config.Configuration;
import de.eldoria.hotsprings.config.HotSpring;
import de.eldoria.hotsprings.config.Limit;
import de.eldoria.hotsprings.config.LimitType;
import de.eldoria.hotsprings.config.Limits;
import de.eldoria.hotsprings.config.SpringSettings;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
        MessageChannel messageChannel = configuration.getSettings().getMessageMode();

        for (Map.Entry<Player, HotSpring> springEntry : register.getActiveHotSprings().entrySet()) {
            Player player = springEntry.getKey();
            HotSpring hotSpring = springEntry.getValue();
            SpringSettings springSettings = configuration.getSpringSettings();

            Material type = player.getLocation().getBlock().getType();

            if (hotSpring.isRequireLava() && hotSpring.isRequireWater()) {
                if (type != Material.LAVA && type != Material.WATER) continue;
            } else {
                if (hotSpring.isRequireWater()) {
                    if (type != Material.WATER) continue;
                }
                if (hotSpring.isRequireLava()) {
                    if (type != Material.LAVA) continue;
                }
            }


            Limit limit = hotSpring.getLimit();
            Limit playerLimit = limits.applyLimit(player, limit);


            if (!playerLimit.canReceive(LimitType.INTERVAL, springSettings)) {
                sender.sendMessage(player, "limit.interval");
                return;
            }

            List<String> messages = new ArrayList<>();
            List<Replacement> replacements = new ArrayList<>();

            for (String command : hotSpring.getCommands()) {
                String replace = command.replace("%player%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replace);
            }

            replacements.add(Replacement.create("PLAYER", player));
            if (playerLimit.canReceive(LimitType.MONEY, springSettings) && economy != null) {
                economy.depositPlayer(player, hotSpring.getMoney());
                messages.add("$granted.money$");
                replacements.add(Replacement.create("MONEY", economy.format(hotSpring.getMoney())));
            } else if (economy != null) {
                messages.add("$limit.money$");
            }


            if (playerLimit.canReceive(LimitType.EXPERIENCE, springSettings)) {
                player.giveExp(hotSpring.getExperience());
                messages.add("$granted.experience$");
                replacements.add(Replacement.create("EXPERIENCE", hotSpring.getExperience()));
            } else {
                messages.add("$limit.experience$");
            }

            sender.sendLocalized(messageChannel, MessageType.NORMAL, player,
                    String.join("\n", messages), replacements.toArray(new Replacement[0]));

            player.playSound(player.getLocation(), configuration.getSettings().getRecieveSound(), SoundCategory.AMBIENT, 1, 1);
        }

        configuration.save();
    }
}
