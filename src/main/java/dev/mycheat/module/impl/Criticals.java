package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/**
 * Критические удары, не прыгая: перед атакой отправляет пакеты позиции с небольшим подъёмом
 * и возвратом (onGround=false), и сервер считает удар нанесённым в падении.
 * Вызывается из ClientPlayerInteractionManagerMixin.
 */
public class Criticals extends Module {
    public Criticals() { super("Criticals", Category.COMBAT, "Критические удары без прыжка: перед ударом отправляет пакеты короткого подскока."); }

    public void onAttack() {
        var p = mc.player;
        if (TPAura.active || p == null || !p.isOnGround() || p.isTouchingWater() || p.isInLava()
                || p.isClimbing() || p.hasVehicle() || p.getAbilities().flying) return;
        double x = p.getX(), y = p.getY(), z = p.getZ();
        p.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.0625, z, false));
        p.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false));
    }
}
