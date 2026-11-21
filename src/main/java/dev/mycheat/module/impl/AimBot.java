package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import dev.mycheat.util.Friends;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/** Aim assistance only: smoothly turns toward the best eligible player; never attacks. */
public class AimBot extends Module {
    private final NumberSetting range = add(new NumberSetting("Range", 6.0, 1.0, 12.0, 0.5));
    private final NumberSetting fov = add(new NumberSetting("FOV", 90.0, 5.0, 180.0, 1.0));
    private final NumberSetting speed = add(new NumberSetting("Speed", 12.0, 1.0, 45.0, 1.0));
    private final NumberSetting ignoreFriends = add(new NumberSetting("IgnoreFriends", 1, 0, 1, 1));
    private final NumberSetting visibleOnly = add(new NumberSetting("VisibleOnly", 1, 0, 1, 1));

    public AimBot() {
        super("AimBot", Category.COMBAT,
                "Плавно наводит взгляд на ближайшего подходящего игрока. Не атакует автоматически.");
    }

    @Override
    public void onTick() {
        var self = mc().player;
        var world = mc().world;
        if (self == null || world == null || mc().currentScreen != null) return;

        PlayerEntity best = null;
        double bestScore = Double.MAX_VALUE;
        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof PlayerEntity target) || target == self || !target.isAlive()
                    || target.isSpectator() || target.isCreative()) continue;
            if (ignoreFriends.getInt() == 1 && Friends.isFriend(target)) continue;
            if (visibleOnly.getInt() == 1 && !self.canSee(target)) continue;

            double distance = self.distanceTo(target);
            if (distance > range.get()) continue;

            Vec3d delta = target.getBoundingBox().getCenter().subtract(self.getEyePos());
            double horizontal = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
            float targetYaw = (float) (Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0);
            float targetPitch = (float) -Math.toDegrees(Math.atan2(delta.y, horizontal));
            float yawDiff = Math.abs(MathHelper.wrapDegrees(targetYaw - self.getYaw()));
            float pitchDiff = Math.abs(MathHelper.wrapDegrees(targetPitch - self.getPitch()));
            double angle = Math.hypot(yawDiff, pitchDiff);
            if (angle > fov.get() * 0.5) continue;

            // Prioritize the player closest to the crosshair, with a small distance tie-breaker.
            double score = angle + distance * 0.05;
            if (score < bestScore) {
                bestScore = score;
                best = target;
            }
        }
        if (best == null) return;

        Vec3d delta = best.getBoundingBox().getCenter().subtract(self.getEyePos());
        double horizontal = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        float wantedYaw = MathHelper.wrapDegrees((float) (Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0));
        float wantedPitch = MathHelper.clamp((float) -Math.toDegrees(Math.atan2(delta.y, horizontal)), -90.0f, 90.0f);
        float step = (float) speed.get();
        float yaw = self.getYaw() + MathHelper.clamp(MathHelper.wrapDegrees(wantedYaw - self.getYaw()), -step, step);
        float pitch = self.getPitch() + MathHelper.clamp(wantedPitch - self.getPitch(), -step * 0.65f, step * 0.65f);
        self.setYaw(yaw);
        self.setPitch(pitch);
    }
}
