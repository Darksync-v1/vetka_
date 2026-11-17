package dev.mycheat.module.impl;

import dev.mycheat.module.Module;
import net.minecraft.util.math.Vec3d;

/**
 * Ходьба по воде в стиле "dolphin": повторяет ванильное плавание вверх (+0.04 за тик, как при зажатом
 * пробеле), поэтому скорость и вертикальная скорость остаются в пределах, которые NoCheatPlus
 * считает нормальными для жидкости. Игрок удерживается у поверхности и слегка "качается".
 * Это не гарантирует обход на любом сервере: зависит от версии и конфига NCP.
 */
public class Jesus extends Module {
    public Jesus() { super("Jesus (NCP)"); }

    @Override public void onTick() {
        var p = mc.player;
        if (p.isSneaking() || p.isFallFlying() || p.getAbilities().flying) return;
        if (p.isTouchingWater() || p.isInLava()) {
            Vec3d v = p.getVelocity();
            if (v.y < 0.1) p.setVelocity(v.x, v.y + 0.04, v.z);
        }
    }
}
