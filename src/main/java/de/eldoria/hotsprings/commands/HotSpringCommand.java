package de.eldoria.hotsprings.commands;

import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.eldoutilities.simplecommands.EldoCommand;
import de.eldoria.eldoutilities.simplecommands.commands.DefaultAbout;
import de.eldoria.hotsprings.commands.hotspringcommand.CreateSpring;
import de.eldoria.hotsprings.commands.hotspringcommand.RemoveSpring;
import de.eldoria.hotsprings.commands.hotspringcommand.ManageSpring;
import de.eldoria.hotsprings.commands.hotspringcommand.Reload;
import de.eldoria.hotsprings.config.Configuration;

public class HotSpringCommand extends EldoCommand {
    public HotSpringCommand(EldoPlugin plugin, Configuration configuration) {
        super(plugin);
        registerCommand("reload", new Reload(plugin));
        registerCommand("about", new DefaultAbout(plugin));
        registerCommand("manageSpring", new ManageSpring(plugin, configuration));
        registerCommand("createSpring", new CreateSpring(plugin, configuration));
        registerCommand("removeSpring", new RemoveSpring(plugin, configuration));
    }
}
