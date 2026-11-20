package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

/** Alternates sneak press/release packets over separate ticks instead of sending both at once. */
public final class Femboy extends Module {
    private final NumberSetting interval = add(new NumberSetting("IntervalTicks", 2, 1, 10, 1));
    private int ticks;
    private boolean serverSneaking;

    public Femboy() {
        super("Femboy", Category.MISC,
                "Чередует нажатие и отпускание Shift пакетами. Сервер может ограничивать или отменять такие пакеты.");
    }

    @Override
    protected void onEnable() {
        ticks = 0;
        serverSneaking = false;
    }

    @Override
    public void onTick() {
        var player = mc().player;
        var network = mc().getNetworkHandler();
        if (player == null || network == null) return;

        if (++ticks < interval.getInt()) return;
        ticks = 0;
        serverSneaking = !serverSneaking;
        network.sendPacket(new ClientCommandC2SPacket(player, serverSneaking
                ? ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY
                : ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
    }

    @Override
    protected void onDisable() {
        var player = mc().player;
        var network = mc().getNetworkHandler();
        if (player != null && network != null && serverSneaking) {
            network.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        }
        serverSneaking = false;
        ticks = 0;
    }
}
