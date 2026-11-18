package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.module.Targets;
import dev.mycheat.setting.NumberSetting;
import dev.mycheat.util.Friends;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Аура для полёта на элитрах. По умолчанию (Chase = 0) НЕ подлетает к цели: только разворачивает
 * камеру и бьёт, если цель уже в пределах Reach, пока ты сам управляешь полётом (ElytraFly).
 * Chase = 1 включает старое поведение: аура сама подлетает к цели.
 */
public class ElytraAura extends Module {
    private final NumberSetting range = add(new NumberSetting("Range", 30, 5, 30, 1));
    private final NumberSetting reach = add(new NumberSetting("Reach", 10.0, 2.0, 15.0, 0.5));
    private final NumberSetting chase = add(new NumberSetting("Chase", 0, 0, 1, 1));
    private final NumberSetting rotate = add(new NumberSetting("Rotate", 1, 0, 1, 1));
    private final NumberSetting speed = add(new NumberSetting("Speed", 1.6, 0.5, 4.0, 0.1));

    public ElytraAura() {
        super("ElytraAura", Category.COMBAT, "В полёте на элитрах бьёт игрока в радиусе Reach (по умолчанию 10, не подлетая). Chase включает старый режим с подлётом, Rotate — автоповорот камеры на цель.");
    }

    @Override public void onTick() {
        var p = mc.player;
        if (!p.isFallFlying()) return;

        Entity target = null;
        double best = range.get() * range.get();
        for (Entity e : mc.world.getEntities()) {
            if (e == p || !(e instanceof PlayerEntity pl) || !pl.isAlive() || pl.isSpectator() || pl.isCreative()
                    || AntiBot.isBot(pl) || Friends.isFriend(pl)) continue;
            double d = e.squaredDistanceTo(p);
            if (d <= best) { best = d; target = e; }
        }
        if (target == null) return;
        Targets.set(target);

        Vec3d eyes = p.getEyePos();
        Vec3d c = target.getBoundingBox().getCenter();
        Vec3d diff = c.subtract(eyes);
        double dist = diff.length();

        if (rotate.getInt() == 1) {
            p.setYaw((float) Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90f);
            p.setPitch(MathHelper.clamp((float) -Math.toDegrees(Math.atan2(diff.y, Math.hypot(diff.x, diff.z))), -90f, 90f));
        }

        if (chase.getInt() == 1) {
            if (dist > 1.5) p.setVelocity(diff.normalize().multiply(Math.min(speed.get(), dist)));
            else p.setVelocity(Vec3d.ZERO);
        }

        if (dist <= reach.get() && p.getAttackCooldownProgress(0.5f) >= 0.95f) {
            mc.interactionManager.attackEntity(p, target);
            p.swingHand(Hand.MAIN_HAND);
        }
    }
}
