package dev.mycheat.module.impl;

import dev.mycheat.module.Module;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class NoFall extends Module {
    public NoFall() { super("NoFall"); }
    @Override public void onTick() {
        if (mc.player.fallDistance > 2.5f && !mc.player.isFallFlying())
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
    }
}
