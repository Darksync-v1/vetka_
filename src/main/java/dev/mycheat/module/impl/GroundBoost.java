package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.util.math.Vec3d;

/**
 * Очень большая скорость по земле в стиле старых читов 1.12.2 — без попытки быть незаметным
 * для современных анти-читов. Почти наверняка палится почти везде, зато быстро.
 */
public class GroundBoost extends Module {
    private final NumberSetting power = add(new NumberSetting("Power", 1.2, 0.5, 3.0, 0.1));

    public GroundBoost() { super("GroundBoost", Category.MOVEMENT, "Очень большая скорость по земле, как в читах 1.12.2. Не для серверов со строгим анти-читом — будет сразу заметно."); }

    @Override public void onTick() {
        var p = mc().player;
        float f = p.input.movementForward, s = p.input.movementSideways;
        if ((f == 0 && s == 0) || !p.isOnGround() || p.isTouchingWater() || p.isInLava()) return;
        double yaw = Math.toRadians(p.getYaw());
        double sin = Math.sin(yaw), cos = Math.cos(yaw);
        double x = s * cos - f * sin, z = f * cos + s * sin, len = Math.hypot(x, z);
        double v = power.get() * TpsSync.scale();
        Vec3d vel = p.getVelocity();
        p.setVelocity(x / len * v, vel.y, z / len * v);
    }
}
