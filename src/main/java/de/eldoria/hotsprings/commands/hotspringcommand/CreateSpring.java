package de.eldoria.hotsprings.commands.hotspringcommand;

import de.eldoria.eldoutilities.localization.Replacement;
import de.eldoria.eldoutilities.messages.MessageChannel;
import de.eldoria.eldoutilities.messages.MessageType;
import de.eldoria.eldoutilities.simplecommands.EldoCommand;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.hotsprings.config.Configuration;
import de.eldoria.hotsprings.config.HotSpring;
import de.eldoria.hotsprings.util.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CreateSpring extends EldoCommand {
    private final Configuration configuration;

    public CreateSpring(Plugin plugin, Configuration configuration) {
        super(plugin);
        this.configuration = configuration;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (denyAccess(sender, Permissions.MANAGE_SPRINGS)) {
            return true;
        }

        if (argumentsInvalid(sender, args, 1, "<$syntax.springName$>")) {
            return true;
        }

        if (configuration.hasHotSpring(args[0])) {
            messageSender().sendLocalized(MessageChannel.CHAT, MessageType.ERROR, sender, "error.alreadyDefined");
            return true;
        }

        configuration.registerHotSpring(new HotSpring(args[0]));
        messageSender().sendLocalized(MessageChannel.CHAT, MessageType.NORMAL, sender, "message.springCreated",
                Replacement.create("NAME", args[0], 'b'));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return TabCompleteUtil.completeFreeInput(args[0], 16, localizer().getMessage("syntax.springName"), localizer());
        }
        return Collections.emptyList();
    }
}
