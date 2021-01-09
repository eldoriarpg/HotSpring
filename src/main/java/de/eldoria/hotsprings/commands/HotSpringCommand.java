package de.eldoria.hotsprings.commands;

import de.eldoria.eldoutilities.simplecommands.EldoCommand;
import de.eldoria.eldoutilities.simplecommands.commands.DefaultAbout;
import de.eldoria.hotsprings.commands.hotspringcommand.Reload;
import org.bukkit.plugin.Plugin;

public class HotSpringCommand extends EldoCommand {
    public HotSpringCommand(Plugin plugin) {
        super(plugin);
        registerCommand("reload", new Reload(plugin));
        registerCommand("about", new DefaultAbout(plugin));
    }
}
