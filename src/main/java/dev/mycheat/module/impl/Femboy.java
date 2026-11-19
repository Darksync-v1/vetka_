package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

/** Rapidly alternates sneak start/stop packets while enabled. */
public final class Femboy extends Module {
    private boolean sneakingPacket;

    public Femboy() {
        super("Femboy", Category.MISC, "Очень быстро нажимает и отпускает Shift, пока функция включена.");
    }

    @Override
    public void onTick() {
        var player = mc().player;
        if (player == null || mc().getNetworkHandler() == null) return;

        sneakingPacket = !sneakingPacket;
        mc().getNetworkHandler().sendPacket(new ClientCommandC2SPacket(
                player,
                sneakingPacket
                        ? ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY
                        : ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY
        ));
        player.setSneaking(sneakingPacket);
    }

    @Override
    protected void onDisable() {
        var player = mc().player;
        if (player != null && mc().getNetworkHandler() != null) {
            mc().getNetworkHandler().sendPacket(new ClientCommandC2SPacket(
                    player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY
            ));
            player.setSneaking(false);
        }
        sneakingPacket = false;
    }
}
