package de.eldoria.hotsprings.commands.hotspringcommand;

import de.eldoria.eldoutilities.inventory.ActionConsumer;
import de.eldoria.eldoutilities.inventory.ActionItem;
import de.eldoria.eldoutilities.inventory.InventoryActionHandler;
import de.eldoria.eldoutilities.inventory.InventoryActions;
import de.eldoria.eldoutilities.items.ItemStackBuilder;
import de.eldoria.eldoutilities.localization.Replacement;
import de.eldoria.eldoutilities.messages.MessageChannel;
import de.eldoria.eldoutilities.messages.MessageType;
import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.eldoutilities.simplecommands.EldoCommand;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.eldoutilities.utils.DataContainerUtil;
import de.eldoria.hotsprings.config.Configuration;
import de.eldoria.hotsprings.config.HotSpring;
import de.eldoria.hotsprings.util.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ManageSpring extends EldoCommand {

    private final Configuration configuration;
    private final InventoryActionHandler actionHandler;
    private final NamespacedKey moneyKey;
    private final NamespacedKey experienceKey;
    private final NamespacedKey waterKey;
    private final NamespacedKey lavaKey;

    public ManageSpring(EldoPlugin plugin, Configuration configuration) {
        super(plugin);
        this.configuration = configuration;
        actionHandler = InventoryActionHandler.create(plugin, configuration::save);
        moneyKey = new NamespacedKey(plugin, "money");
        experienceKey = new NamespacedKey(plugin, "experience");
        waterKey = new NamespacedKey(plugin, "water");
        lavaKey = new NamespacedKey(plugin, "lava");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(denyAccess(sender, Permissions.MANAGE_SPRINGS)){
            return true;
        }

        if (argumentsInvalid(sender, args, 1, "<$syntax.springName$>")) {
            return true;
        }

        if (!configuration.hasHotSpring(args[0])) {
            messageSender().sendLocalized(MessageChannel.CHAT, MessageType.ERROR, sender, "error.invalidSpring",
                    Replacement.create("VALUES", configuration.getHotSpringNames(), 'b'));
            return true;
        }

        HotSpring hotSpring = configuration.getHotSpring(args[0]);

        Inventory inventory = Bukkit.createInventory((getPlayerFromSender(sender)), 9, "Manage " + hotSpring.getName());

        InventoryActions wrap = actionHandler.wrap(getPlayerFromSender(sender), inventory,
                event -> messageSender().sendLocalized(
                        MessageChannel.CHAT,
                        MessageType.NORMAL,
                        sender,
                        "message.springSaved",
                        Replacement.create("NAME", hotSpring.getName(), 'b')));

        wrap.addAction(
                new ActionItem(
                        ItemStackBuilder.of(Material.GOLD_INGOT)
                                .withDisplayName(localizer().getMessage("value.money"))
                                .withLore(String.valueOf(hotSpring.getMoney()))
                                .withNBTData(c -> c.set(moneyKey, PersistentDataType.INTEGER, hotSpring.getMoney()))
                                .build(),
                        2,
                        ActionConsumer.getIntRange(moneyKey, 0, 100000),
                        itemStack ->
                        {
                            PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
                            hotSpring.setMoney(container.get(moneyKey, PersistentDataType.INTEGER));
                        }));
        wrap.addAction(
                new ActionItem(
                        ItemStackBuilder.of(Material.EXPERIENCE_BOTTLE)
                                .withDisplayName(localizer().getMessage("value.experience"))
                                .withLore(String.valueOf(hotSpring.getExperience()))
                                .withNBTData(c -> c.set(experienceKey, PersistentDataType.INTEGER, hotSpring.getExperience()))
                                .build(),
                        3,
                        ActionConsumer.getIntRange(experienceKey, 0, 100000),
                        itemStack ->
                        {
                            PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
                            hotSpring.setExperience(container.get(experienceKey, PersistentDataType.INTEGER));
                        }));

        wrap.addAction(
                new ActionItem(
                        ItemStackBuilder.of(Material.WATER_BUCKET)
                                .withDisplayName(localizer().getMessage("value.requireWater"))
                                .withLore(hotSpring.isRequireWater() ? "§2true" : "§cfalse")
                                .withNBTData(c -> c.set(waterKey, PersistentDataType.BYTE, DataContainerUtil.booleanToByte(hotSpring.isRequireWater())))
                                .build(),
                        4,
                        ActionConsumer.booleanToggle(waterKey),
                        itemStack ->
                        {
                            PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
                            hotSpring.setRequireWater(DataContainerUtil.byteToBoolean(container.get(waterKey, PersistentDataType.BYTE)));
                        }));

        wrap.addAction(
                new ActionItem(
                        ItemStackBuilder.of(Material.LAVA_BUCKET)
                                .withDisplayName(localizer().getMessage("value.requireLava"))
                                .withLore(hotSpring.isRequireLava() ? "§2true" : "§cfalse")
                                .withNBTData(c -> c.set(lavaKey, PersistentDataType.BYTE, DataContainerUtil.booleanToByte(hotSpring.isRequireLava())))
                                .build(),
                        5,
                        ActionConsumer.booleanToggle(lavaKey),
                        itemStack ->
                        {
                            PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
                            hotSpring.setRequireLava(DataContainerUtil.byteToBoolean(container.get(lavaKey, PersistentDataType.BYTE)));
                        }));

        getPlayerFromSender(sender).openInventory(inventory);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return TabCompleteUtil.complete(args[0], configuration.getHotSprings(), HotSpring::getName);
        }
        return Collections.emptyList();
    }
}
