package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.module.Targets;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/** Сам ходит кругами вокруг цели ауры, без необходимости держать W. */
public class TargetStrafe extends Module {
    private final NumberSetting radius = add(new NumberSetting("Radius", 2.6, 1.0, 6.0, 0.1));
    private final NumberSetting speed = add(new NumberSetting("Speed", 0.26, 0.1, 0.5, 0.01));
    private final NumberSetting jump = add(new NumberSetting("Jump", 0, 0, 1, 1));
    private final NumberSetting reverse = add(new NumberSetting("Reverse", 0, 0, 1, 1));
    private final NumberSetting smooth = add(new NumberSetting("Smooth", 0.35, 0.0, 0.9, 0.05));
    private final NumberSetting closeSpeed = add(new NumberSetting("CloseSpeed", 0.6, 0.1, 1.5, 0.05));
    private int dir = 1;

    public TargetStrafe() {
        super("TargetStrafe", Category.COMBAT,
                "Сам ходит кругами вокруг цели ауры (держать W не нужно). Radius — желаемая дистанция, " +
                "Speed — скорость по кругу, CloseSpeed — как быстро подходит/отходит до нужной дистанции, " +
                "Smooth — плавность поворота (0 = резко, выше = мягче), Reverse — сменить направление, " +
                "Jump — прыгать на месте во время кружения.");
    }

    @Override public void onTick() {
        Entity t = Targets.get();
        var p = mc().player;
        if (t == null || p.isTouchingWater()) return;
        if (p.horizontalCollision) dir = -dir;
        int d = reverse.getInt() == 1 ? -dir : dir;

        double dx = p.getX() - t.getX(), dz = p.getZ() - t.getZ();
        double dist = Math.hypot(dx, dz);
        if (dist < 0.01) return;
        double nx = dx / dist, nz = dz / dist;                       // от цели к игроку
        double k = MathHelper.clamp((dist - radius.get()) * closeSpeed.get(), -1, 1); // >0: далеко, идём к цели
        double mx = -nz * d - nx * k, mz = nx * d - nz * k;
        double len = Math.hypot(mx, mz);
        if (len == 0) return;

        double tx = mx / len * speed.get(), tz = mz / len * speed.get();
        Vec3d v = p.getVelocity();
        double s = smooth.get();
        p.setVelocity(v.x * s + tx * (1 - s), v.y, v.z * s + tz * (1 - s));
        if (jump.getInt() == 1 && p.isOnGround()) p.jump();
    }
}
