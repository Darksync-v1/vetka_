package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.module.Targets;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/** Кружение вокруг текущей цели Aura с настраиваемой скоростью, дистанцией и бустом при атаке. */
public class TargetStrafe extends Module {
    private final NumberSetting speed = add(new NumberSetting("Speed", 0.26, 0.05, 0.8, 0.01));
    private final NumberSetting radius = add(new NumberSetting("Radius", 2.6, 1.0, 6.0, 0.1));
    // 0 = выключен, 1 = включён. NumberSetting используется как переключатель в текущем ClickGUI.
    private final NumberSetting hitBoost = add(new NumberSetting("HitBoost", 0, 0, 1, 1));
    private int direction = 1;

    public TargetStrafe() {
        super("TargetStrafe", Category.COMBAT,
                "Кружится вокруг выбранной цели Aura. Speed — скорость движения по кругу; " +
                "Radius — желаемая дистанция до цели; HitBoost — дополнительный импульс при ударе.");
    }

    @Override
    public void onTick() {
        Entity target = Targets.get();
        var player = mc().player;
        if (target == null || player == null || player.isTouchingWater()) return;
        if (player.horizontalCollision) direction = -direction;

        double dx = player.getX() - target.getX();
        double dz = player.getZ() - target.getZ();
        double distance = Math.hypot(dx, dz);
        if (distance < 0.01) return;

        double nx = dx / distance;
        double nz = dz / distance;
        // Корректируем радиальную составляющую, чтобы удерживать заданный Radius.
        double radial = MathHelper.clamp((distance - radius.get()) * 0.6, -0.6, 0.6);
        double mx = -nz * direction - nx * radial;
        double mz = nx * direction - nz * radial;
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

        double tangentX = -dz / distance * direction;
        double tangentZ = dx / distance * direction;
        Vec3d velocity = player.getVelocity();
        double impulse = Math.max(0.12, speed.get() * 0.75);
        player.setVelocity(velocity.x + tangentX * impulse, velocity.y, velocity.z + tangentZ * impulse);
    }
}
