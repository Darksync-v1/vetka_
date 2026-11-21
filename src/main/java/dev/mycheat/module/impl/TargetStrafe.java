package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.module.Targets;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/** Движение вокруг цели, выбранной Aura и прошедшей её FOV-проверку. */
public class TargetStrafe extends Module {
    private final NumberSetting speed = add(new NumberSetting("Speed", 0.26, 0.05, 0.8, 0.01));
    private final NumberSetting radius = add(new NumberSetting("Radius", 2.6, 1.0, 6.0, 0.1));
    // Direction: 0=Auto, 1=Right, 2=Left, 3=Toward, 4=Away.
    private final NumberSetting directionMode = add(new NumberSetting("Direction", 0, 0, 4, 1));
    private final NumberSetting hitBoost = add(new NumberSetting("HitBoost", 0, 0, 1, 1));
    private int direction = 1;

    public TargetStrafe() {
        super("TargetStrafe", Category.COMBAT,
                "Двигается вокруг цели Aura только пока она выбрана в FOV ауры. Direction: 0=Auto, 1=Right, 2=Left, 3=Toward, 4=Away.");
    }

    @Override
    public void onTick() {
        Entity target = Targets.get();
        var player = mc().player;
        if (player == null || player.isTouchingWater()) return;
        // Targets публикуется KillAura только после проверки дистанции и FOV.
        if (target == null || !target.isAlive()) return;

        if (player.horizontalCollision && directionMode.getInt() == 0) direction = -direction;
        int mode = directionMode.getInt();
        int side = mode == 1 ? 1 : mode == 2 ? -1 : direction;

        double dx = player.getX() - target.getX();
        double dz = player.getZ() - target.getZ();
        double distance = Math.hypot(dx, dz);
        if (distance < 0.01) return;
        double nx = dx / distance;
        double nz = dz / distance;

        double radial = MathHelper.clamp((distance - radius.get()) * 0.6, -0.6, 0.6);
        double mx;
        double mz;
        if (mode == 3) { // Подойти к цели
            mx = -nx;
            mz = -nz;
        } else if (mode == 4) { // Отойти от цели
            mx = nx;
            mz = nz;
        } else {
            mx = -nz * side - nx * radial;
            mz = nx * side - nz * radial;
        }
        double length = Math.hypot(mx, mz);
        if (length < 1.0E-6) return;
        double vx = mx / length * speed.get();
        double vz = mz / length * speed.get();
        Vec3d velocity = player.getVelocity();
        player.setVelocity(vx, velocity.y, vz);
    }

    /** Вызывается миксином при атаке; усиливает касательную скорость только по текущей цели. */
    public void onTargetAttack(Entity target) {
        if (!isEnabled() || hitBoost.getInt() != 1 || target == null || target != Targets.get()) return;
        var player = mc().player;
        if (player == null) return;
        double dx = player.getX() - target.getX();
        double dz = player.getZ() - target.getZ();
        double distance = Math.hypot(dx, dz);
        if (distance < 1.0E-4) return;
        int side = directionMode.getInt() == 2 ? -1 : direction;
        double tangentX = -dz / distance * side;
        double tangentZ = dx / distance * side;
        Vec3d velocity = player.getVelocity();
        double impulse = Math.max(0.12, speed.get() * 0.75);
        player.setVelocity(velocity.x + tangentX * impulse, velocity.y, velocity.z + tangentZ * impulse);
    }
}
