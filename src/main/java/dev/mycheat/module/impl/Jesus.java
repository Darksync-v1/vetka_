package dev.mycheat.module.impl;

import dev.mycheat.MyCheat;
import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.util.math.Vec3d;

/**
 * Mode 0: «дельфин», ванильное всплытие +0.04 за тик.
 * Mode 1: твёрдая вода. FluidBlockMixin возвращает полную кубическую коллизию
 * для водных блоков под игроком, чтобы по поверхности можно было ходить.
 */
public class Jesus extends Module {
    private final NumberSetting mode = add(new NumberSetting("Mode", 1, 0, 1, 1));
    private final NumberSetting speed = add(new NumberSetting("Speed", 0.15, 0.05, 0.3, 0.01));

    public Jesus() {
        super("Jesus", Category.MOVEMENT, "Хождение по воде. Mode 1 — вода становится твёрдой поверхностью; Mode 0 — всплытие как дельфин. Shift — отключить коллизию воды.");
    }

    public static boolean solid() {
        Jesus j = MyCheat.MODULES.get(Jesus.class);
        return j != null && j.isEnabled() && j.mode.getInt() == 1;
    }

    @Override public void onTick() {
        var p = mc().player;
        if (p.isSneaking() || p.isFallFlying() || p.getAbilities().flying) return;
        Vec3d v = p.getVelocity();

        if (mode.getInt() == 0) {
            if ((p.isTouchingWater() || p.isInLava()) && v.y < 0.1) p.setVelocity(v.x, v.y + 0.04, v.z);
            return;
        }
        if (p.isTouchingWater() && p.isOnGround()) {
            double x = 0, z = 0;
            float f = p.input.movementForward, s = p.input.movementSideways;
            if (f != 0 || s != 0) {
                double yaw = Math.toRadians(p.getYaw());
                double sin = Math.sin(yaw), cos = Math.cos(yaw);
                double dx = s * cos - f * sin, dz = f * cos + s * sin, len = Math.hypot(dx, dz);
                x = dx / len * speed.get();
                z = dz / len * speed.get();
            }
            p.setVelocity(x, Math.max(0, v.y), z);
        } else if (p.isTouchingWater() && v.y < 0.1) {
            p.setVelocity(v.x, v.y + 0.04, v.z); // всплываем до поверхности
        }
    }
}
