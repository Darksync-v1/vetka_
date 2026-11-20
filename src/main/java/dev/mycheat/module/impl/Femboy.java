package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

/** Sends rapid sneak start/stop transitions while enabled. */
public final class Femboy extends Module {
    public Femboy() {
        super("Femboy", Category.MISC, "Быстро отправляет на сервер нажатие и отпускание Shift.");
    }

    @Override
    public void onTick() {
        var player = mc().player;
        var network = mc().getNetworkHandler();
        if (player == null || network == null) return;

        // A complete transition pair per tick is more reliable than leaving the
        // client-side sneak flag alternating and potentially fighting the real key.
        network.sendPacket(new ClientCommandC2SPacket(
                player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        network.sendPacket(new ClientCommandC2SPacket(
                player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
    }

    @Override
    protected void onDisable() {
        var player = mc().player;
        var network = mc().getNetworkHandler();
        if (player != null && network != null) {
            network.sendPacket(new ClientCommandC2SPacket(
                    player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        }
        if (player != null) player.setSneaking(false);
    }
}
