package dev.mycheat.module.impl;

import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Аура для полёта на элитрах: ищет игрока в радиусе Range (до 30), сама подлетает к нему
 * и бьёт, когда цель в пределах Reach. Бьёт только вблизи: сервер не примет удар с 30 блоков.
 */
public class ElytraAura extends Module {
    private final NumberSetting range = add(new NumberSetting("Range", 30, 5, 30, 1));
    private final NumberSetting speed = add(new NumberSetting("Speed", 1.6, 0.5, 4.0, 0.1));
    private final NumberSetting reach = add(new NumberSetting("Reach", 10.0, 2.0, 12.0, 0.5));

    public ElytraAura() { super("ElytraAura"); }

    @Override public void onTick() {
        var p = mc.player;
        if (!p.isFallFlying()) return;

        Entity target = null;
        double best = range.get() * range.get();
        for (Entity e : mc.world.getEntities()) {
            if (e == p || !(e instanceof PlayerEntity pl) || !pl.isAlive() || pl.isSpectator() || pl.isCreative()) continue;
            double d = e.squaredDistanceTo(p);
            if (d <= best) { best = d; target = e; }
        }
        if (target == null) return;

        Vec3d eyes = p.getEyePos();
        Vec3d c = target.getBoundingBox().getCenter();
        Vec3d diff = c.subtract(eyes);
        double dist = diff.length();

        p.setYaw((float) Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90f);
        p.setPitch(MathHelper.clamp((float) -Math.toDegrees(Math.atan2(diff.y, Math.hypot(diff.x, diff.z))), -90f, 90f));

        // подлетаем, у самой цели притормаживаем, чтобы не пролетать мимо
        if (dist > 1.5) p.setVelocity(diff.normalize().multiply(Math.min(speed.get(), dist)));
        else p.setVelocity(Vec3d.ZERO);

        if (dist <= reach.get() && p.getAttackCooldownProgress(0.5f) >= 0.95f) {
            mc.interactionManager.attackEntity(p, target);
            p.swingHand(Hand.MAIN_HAND);
        }
    }
}
