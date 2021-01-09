package de.eldoria.hotsprings.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import de.eldoria.hotsprings.worldguard.HotSpringRegister;
import de.eldoria.hotsprings.worldguard.HotSpringFlag;

public class HotSpringFlagHandler extends FlagValueChangeHandler<String> {

    private final HotSpringRegister register;

    protected HotSpringFlagHandler(Session session, Flag<String> flag, HotSpringRegister register) {
        super(session, flag);
        this.register = register;
    }

    @Override
    protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, String value) {
        if(value != null){
            register.registerPlayer(BukkitAdapter.adapt(player), value);
        }
    }

    @Override
    protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, String currentValue, String lastValue, MoveType moveType) {
        register.registerPlayer(BukkitAdapter.adapt(player), currentValue);
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, String lastValue, MoveType moveType) {
        register.unregisterPlayer(BukkitAdapter.adapt(player));
        return true;
    }

    public static class Factory extends Handler.Factory<HotSpringFlagHandler> {
        private final HotSpringFlag flag;
        private final HotSpringRegister register;

        public Factory(HotSpringFlag flag, HotSpringRegister register) {
            this.flag = flag;
            this.register = register;
        }

        @Override
        public HotSpringFlagHandler create(Session session) {
            return new HotSpringFlagHandler(session, flag, register);
        }
    }
}
