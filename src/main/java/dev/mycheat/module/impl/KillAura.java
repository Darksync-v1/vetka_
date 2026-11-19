package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.module.Targets;
import dev.mycheat.setting.NumberSetting;
import dev.mycheat.util.Friends;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/** Выбирает цель, silent-ротация пакетом, бьёт по восстановленному кулдауну. */
public class KillAura extends Module {
    private final NumberSetting range = add(new NumberSetting("Range", 5.0, 2.0, 6.0, 0.1));
    private final NumberSetting players = add(new NumberSetting("Players", 1, 0, 1, 1));
    private final NumberSetting mobs = add(new NumberSetting("Mobs", 1, 0, 1, 1));
    private final NumberSetting fov = add(new NumberSetting("FOV", 180, 20, 180, 5));
    private final NumberSetting cdReq = add(new NumberSetting("CDReq", 0.95, 0.5, 1.0, 0.05));
    private final NumberSetting sticky = add(new NumberSetting("Sticky", 0, 0, 1, 1));
    private final NumberSetting priority = add(new NumberSetting("Priority", 0, 0, 1, 1)); // 0=ближе, 1=меньше HP
    private Entity current;

    public KillAura() {
        super("Aura", Category.COMBAT,
                "Автоматически бьёт цель в радиусе Range. FOV — максимальный угол от направления взгляда до цели " +
                "(180 = без ограничения, меньше — цель должна быть почти перед тобой, выглядит естественнее). " +
                "CDReq — какую долю кулдауна атаки ждать перед ударом (ниже = чаще бьёт, но менее естественно). " +
                "Sticky — не отпускать текущую цель, пока она жива и в радиусе, вместо постоянного переключения. " +
                "Priority — выбирать ближайшую цель или с наименьшим здоровьем. Друзья и боты пропускаются.");
    }

    private boolean valid(Entity e) {
        if (e == mc().player || !(e instanceof LivingEntity le) || !le.isAlive()) return false;
        if (e instanceof PlayerEntity pl) return players.getInt() == 1 && !AntiBot.isBot(e) && !Friends.isFriend(e) && !pl.isSpectator() && !pl.isCreative();
        return mobs.getInt() == 1 && e instanceof HostileEntity;
    }

    private boolean inFov(Entity e, Vec3d eyes, Vec3d look) {
        if (fov.get() >= 179) return true;
        Vec3d toEnt = e.getBoundingBox().getCenter().subtract(eyes).normalize();
        double angle = Math.toDegrees(Math.acos(MathHelper.clamp(toEnt.dotProduct(look), -1, 1)));
        return angle <= fov.get();
    }

    @Override public void onTick() {
        var p = mc().player;
        if (mc().currentScreen != null || p.getAttackCooldownProgress(0.5f) < cdReq.get()) return;

        Vec3d eyes = p.getEyePos();
        Vec3d look = p.getRotationVec(1.0f);
        double rangeSq = range.get() * range.get();

        if (sticky.getInt() == 1 && current != null) {
            if (!valid(current) || current.squaredDistanceTo(p) > rangeSq) current = null;
        } else current = null;

        if (current == null) {
            Entity best = null;
            double bestScore = Double.MAX_VALUE;
            for (Entity e : mc().world.getEntities()) {
                if (!valid(e) || !inFov(e, eyes, look)) continue;
                double dx = MathHelper.clamp(eyes.x, e.getBoundingBox().minX, e.getBoundingBox().maxX) - eyes.x;
                double dy = MathHelper.clamp(eyes.y, e.getBoundingBox().minY, e.getBoundingBox().maxY) - eyes.y;
                double dz = MathHelper.clamp(eyes.z, e.getBoundingBox().minZ, e.getBoundingBox().maxZ) - eyes.z;
                double d = dx * dx + dy * dy + dz * dz;
                if (d > rangeSq) continue;
                double score = priority.getInt() == 1 && e instanceof LivingEntity le ? le.getHealth() : d;
                if (score < bestScore) { bestScore = score; best = e; }
            }
            current = best;
        }
        if (current == null) return;
        Targets.set(current);

        Vec3d c = current.getBoundingBox().getCenter();
        double dx = c.x - eyes.x, dy = c.y - eyes.y, dz = c.z - eyes.z;
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.hypot(dx, dz)));

        // silent rotation: сервер видит поворот, камера не дёргается
        p.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
                MathHelper.wrapDegrees(yaw), MathHelper.clamp(pitch, -90f, 90f), p.isOnGround()));
        mc().interactionManager.attackEntity(p, current);
        p.swingHand(Hand.MAIN_HAND);
    }
}
