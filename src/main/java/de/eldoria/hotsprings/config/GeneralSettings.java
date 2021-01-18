package de.eldoria.hotsprings.config;

import de.eldoria.eldoutilities.messages.MessageChannel;
import de.eldoria.eldoutilities.messages.channeldata.ChannelData;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.eldoutilities.serialization.TypeResolvingMap;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Getter
@SerializableAs("hotSpringGeneralSettings")
public class GeneralSettings implements ConfigurationSerializable {
    private String messageMode = "SUBTITLE";
    private Sound recieveSound = Sound.ENTITY_PUFFER_FISH_BLOW_UP;

    public GeneralSettings() {
    }

    public GeneralSettings(Map<String, Object> objectMap) {
        TypeResolvingMap map = SerializationUtil.mapOf(objectMap);
        messageMode = map.getValueOrDefault("messageMode", messageMode);
        recieveSound = map.getValueOrDefault("receiveSound", recieveSound, Sound.class);
    }

    public MessageChannel<? extends ChannelData> getMessageMode() {
        return MessageChannel.getChannelByNameOrDefault(messageMode);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("messageMode", messageMode)
                .add("recieveSound", recieveSound)
                .build();
    }
}
