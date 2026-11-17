package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.module.Targets;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class TargetStrafe extends Module {
    private final NumberSetting radius = add(new NumberSetting("Radius", 2.6, 1.5, 5.0, 0.1));
    private final NumberSetting speed = add(new NumberSetting("Speed", 0.26, 0.15, 0.5, 0.01));
    private final NumberSetting jump = add(new NumberSetting("Jump", 0, 0, 1, 1));
    private int dir = 1;

    public TargetStrafe() {
        super("TargetStrafe", Category.COMBAT, "Бегает кругами вокруг цели ауры, пока зажат W. Сменяет сторону при упоре в стену.");
    }

    @Override public void onTick() {
        Entity t = Targets.get();
        var p = mc.player;
        if (t == null || p.input.movementForward <= 0 || p.isTouchingWater()) return;
        if (p.horizontalCollision) dir = -dir;
        double dx = p.getX() - t.getX(), dz = p.getZ() - t.getZ();
        double dist = Math.hypot(dx, dz);
        if (dist < 0.01) return;
        double nx = dx / dist, nz = dz / dist;                       // от цели к игроку
        double k = MathHelper.clamp((dist - radius.get()) * 0.6, -1, 1); // >0: далеко, идём к цели
        double mx = -nz * dir - nx * k, mz = nx * dir - nz * k;
        double len = Math.hypot(mx, mz);
        if (len == 0) return;
        p.setVelocity(mx / len * speed.get(), p.getVelocity().y, mz / len * speed.get());
        if (jump.getInt() == 1 && p.isOnGround()) p.jump();
    }
}
