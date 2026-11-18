package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import java.util.ArrayList;
import java.util.List;

/** Копит пакеты движения и отправляет пачкой раз в Delay тиков. Перехват в ClientPlayNetworkHandlerMixin. */
public class FakeLag extends Module {
    private final NumberSetting delay = add(new NumberSetting("Delay", 10, 2, 40, 1));
    private final List<Packet<?>> queue = new ArrayList<>();
    private boolean flushing;
    private int ticks;

    public FakeLag() {
        super("FakeLag", Category.MISC, "Задерживает пакеты движения и отправляет пачкой: для других ты лагаешь и «телепортируешься». Слайдер Delay — задержка в тиках.");
    }

    public boolean intercept(Packet<?> p) {
        if (flushing || !(p instanceof PlayerMoveC2SPacket)) return false;
        queue.add(p);
        return true;
    }

    private void flush() {
        if (mc().player == null) { queue.clear(); return; }
        flushing = true;
        for (Packet<?> p : queue) mc().player.networkHandler.sendPacket(p);
        queue.clear();
        flushing = false;
    }

    @Override public void onTick() {
        if (++ticks >= delay.getInt()) { ticks = 0; flush(); }
    }
    @Override protected void onDisable() { flush(); ticks = 0; }
}
