package de.eldoria.hotsprings.commands.hotspringcommand;

import de.eldoria.eldoutilities.simplecommands.EldoCommand;
import de.eldoria.hotsprings.HotSprings;
import de.eldoria.hotsprings.util.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;


public class Reload extends EldoCommand {
    public Reload(Plugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (denyAccess(sender, Permissions.RELOAD)) {
            return true;
        }
        HotSprings.getInstance(HotSprings.class).onEnable();
        messageSender().sendLocalizedMessage(sender, "reload.success");
        HotSprings.logger().info("HotSprings reloaded!");
        return true;
    }
}
