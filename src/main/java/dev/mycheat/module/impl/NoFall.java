package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class NoFall extends Module {
    private final NumberSetting threshold = add(new NumberSetting("Threshold", 2.5, 1.0, 5.0, 0.5));

    public NoFall() { super("NoFall", Category.MOVEMENT, "Отключает урон от падения. Threshold — с какой высоты падения срабатывает."); }

    @Override public void onTick() {
        if (mc().player.fallDistance > threshold.get() && !mc().player.isFallFlying())
            mc().player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
    }
}
