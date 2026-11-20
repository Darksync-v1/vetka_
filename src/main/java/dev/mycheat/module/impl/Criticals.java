package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/**
 * Критические удары, не прыгая: перед атакой отправляет пакеты позиции с небольшим подъёмом
 * и возвратом (onGround=false), и сервер считает удар нанесённым в падении.
 * Вызывается из ClientPlayerInteractionManagerMixin.
 */
public class Criticals extends Module {
    private final NumberSetting bounce = add(new NumberSetting("Bounce", 0.0625, 0.02, 0.2, 0.0025));

    public Criticals() { super("Criticals", Category.COMBAT, "Критические удары без прыжка. Bounce — высота фейкового подскока перед ударом."); }

    public void onAttack() {
        var p = mc().player;
        if (p == null || !p.isOnGround() || p.isTouchingWater() || p.isInLava()
                || p.isClimbing() || p.hasVehicle() || p.getAbilities().flying) return;
        double x = p.getX(), y = p.getY(), z = p.getZ();
        p.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + bounce.get(), z, false));
        p.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false));
    }
}
