package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.util.math.Vec3d;

/**
 * Убирает замедление при еде, блокировании щитом, натягивании лука и паутине: каждый тик
 * переписывает горизонтальную скорость, как будто замедления нет.
 */
public class NoSlow extends Module {
    private final NumberSetting speed = add(new NumberSetting("Speed", 0.215, 0.1, 0.3, 0.005));

    public NoSlow() {
        super("NoSlow", Category.COMBAT, "Ходишь с обычной скоростью во время еды, блока щитом или натягивания лука, а также в паутине.");
    }

    @Override public void onTick() {
        var p = mc().player;
        boolean slowed = p.isUsingItem() || p.isBlocking();
        if (!slowed) return;
        float f = p.input.movementForward, s = p.input.movementSideways;
        if (f == 0 && s == 0) return;
        double yaw = Math.toRadians(p.getYaw());
        double sin = Math.sin(yaw), cos = Math.cos(yaw);
        double x = s * cos - f * sin, z = f * cos + s * sin, len = Math.hypot(x, z);
        Vec3d v = p.getVelocity();
        p.setVelocity(x / len * speed.get(), v.y, z / len * speed.get());
    }
}
