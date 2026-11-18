package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/** Выбирает ближайшую цель, silent-ротация пакетом, бьёт по восстановленному кулдауну. */
public class KillAura extends Module {
    private final NumberSetting range = add(new NumberSetting("Range", 5.0, 2.0, 6.0, 0.1));
    private final NumberSetting players = add(new NumberSetting("Players", 1, 0, 1, 1));
    private final NumberSetting mobs = add(new NumberSetting("Mobs", 1, 0, 1, 1));

    public KillAura() { super("KillAura", Category.COMBAT, "Автоматически бьёт ближайшую цель в радиусе Range. Поворот уходит пакетом (камера не дёргается), удар по кулдауну. Друзья и боты пропускаются."); }

    private boolean valid(Entity e) {
        if (e == mc().player || !(e instanceof LivingEntity le) || !le.isAlive()) return false;
        if (e instanceof PlayerEntity pl) return players.getInt() == 1 && !AntiBot.isBot(e) && !dev.mycheat.util.Friends.isFriend(e) && !pl.isSpectator() && !pl.isCreative();
        return mobs.getInt() == 1 && e instanceof HostileEntity;
    }

    @Override public void onTick() {
        var p = mc().player;
        if (mc().currentScreen != null || p.getAttackCooldownProgress(0.5f) < 0.95f) return;

        Entity target = null;
        double best = range.get() * range.get();
        Vec3d eyes = p.getEyePos();
        for (Entity e : mc().world.getEntities()) {
            if (!valid(e)) continue;
            double d = e.getBoundingBox().getCenter().squaredDistanceTo(eyes);
            // дистанция до ближайшей точки хитбокса, как считает сервер
            double dx = MathHelper.clamp(eyes.x, e.getBoundingBox().minX, e.getBoundingBox().maxX) - eyes.x;
            double dy = MathHelper.clamp(eyes.y, e.getBoundingBox().minY, e.getBoundingBox().maxY) - eyes.y;
            double dz = MathHelper.clamp(eyes.z, e.getBoundingBox().minZ, e.getBoundingBox().maxZ) - eyes.z;
            d = dx * dx + dy * dy + dz * dz;
            if (d <= best) { best = d; target = e; }
        }
        if (target == null) return;
        dev.mycheat.module.Targets.set(target);

        Vec3d c = target.getBoundingBox().getCenter();
        double dx = c.x - eyes.x, dy = c.y - eyes.y, dz = c.z - eyes.z;
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.hypot(dx, dz)));

        // silent rotation: сервер видит поворот, камера не дёргается
        p.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
                MathHelper.wrapDegrees(yaw), MathHelper.clamp(pitch, -90f, 90f), p.isOnGround()));
        mc().interactionManager.attackEntity(p, target);
        p.swingHand(Hand.MAIN_HAND);
    }
}
