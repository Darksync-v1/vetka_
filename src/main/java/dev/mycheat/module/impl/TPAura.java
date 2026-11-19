package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.module.Targets;
import dev.mycheat.setting.NumberSetting;
import dev.mycheat.util.Friends;
import dev.mycheat.util.Tp;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

/** Packet-step teleport aura adapted from the behavior/settings of Vinted 3.1 TPAura. */
public class TPAura extends Module {
    public static boolean active;

    private final NumberSetting players = add(new NumberSetting("Players", 1, 0, 1, 1));
    private final NumberSetting friends = add(new NumberSetting("Friends", 0, 0, 1, 1));
    private final NumberSetting mobs = add(new NumberSetting("Mobs", 0, 0, 1, 1));
    private final NumberSetting animals = add(new NumberSetting("Animals", 0, 0, 1, 1));
    private final NumberSetting invisible = add(new NumberSetting("Invisible", 0, 0, 1, 1));
    private final NumberSetting attackRange = add(new NumberSetting("AttackRange", 15, 2.5, 32, 0.5));
    private final NumberSetting attackDelay = add(new NumberSetting("AttackDelay", 10, 1, 20, 1));
    private final NumberSetting step = add(new NumberSetting("Step", 8, 1, 10, 0.5));
    private final NumberSetting safeDistance = add(new NumberSetting("SafeDistance", 0.5, 0.1, 2, 0.1));
    private final NumberSetting phase = add(new NumberSetting("Phase", 0, 0, 1, 1));
    private final NumberSetting rotate = add(new NumberSetting("Rotate", 1, 0, 1, 1));
    private final NumberSetting onlyAura = add(new NumberSetting("OnlyWithAura", 0, 0, 1, 1));

    private int attackTicks;

    public TPAura() {
        super("TPAura", Category.COMBAT,
                "Телепорт-подход к цели, атака и возврат. Настройки адаптированы по TPAura из Vinted 3.1.");
    }

    @Override protected void onEnable() { attackTicks = 0; }
    @Override protected void onDisable() { active = false; }

    private boolean valid(Entity entity) {
        if (!(entity instanceof LivingEntity living) || entity == mc().player || !living.isAlive()) return false;
        if (living.isInvisible() && invisible.getInt() == 0) return false;
        if (entity instanceof PlayerEntity player) {
            if (player.isSpectator() || player.isCreative() || AntiBot.isBot(player)) return false;
            return Friends.isFriend(player) ? friends.getInt() == 1 : players.getInt() == 1;
        }
        if (entity instanceof HostileEntity) return mobs.getInt() == 1;
        if (entity instanceof AnimalEntity) return animals.getInt() == 1;
        return false;
    }

    @Override public void onTick() {
        var player = mc().player;
        var world = mc().world;
        if (player == null || world == null || mc().interactionManager == null || player.networkHandler == null) return;
        if (attackTicks > 0) { attackTicks--; return; }
        if (onlyAura.getInt() == 1) {
            KillAura aura = dev.mycheat.MyCheat.MODULES.get(KillAura.class);
            if (aura == null || !aura.isEnabled()) return;
        }
        if (player.getAttackCooldownProgress(0.5f) < 0.9f) return;

        Entity target = null;
        double best = attackRange.get() * attackRange.get();
        for (Entity entity : world.getEntities()) {
            if (!valid(entity)) continue;
            double distance = entity.squaredDistanceTo(player);
            if (distance <= best) { best = distance; target = entity; }
        }
        if (!(target instanceof LivingEntity)) return;
        Targets.set(target);

        Vec3d original = player.getPos();
        Vec3d targetPos = target.getPos();
        Vec3d horizontal = new Vec3d(original.x - targetPos.x, 0, original.z - targetPos.z);
        if (horizontal.lengthSquared() < 1.0e-6) horizontal = new Vec3d(1, 0, 0);
        else horizontal = horizontal.normalize();
        Vec3d destination = new Vec3d(
                targetPos.x + horizontal.x * safeDistance.get(),
                targetPos.y,
                targetPos.z + horizontal.z * safeDistance.get());

        // Without Phase, avoid selecting a destination whose player-sized box is blocked.
        if (phase.getInt() == 0) {
            var box = player.getBoundingBox().offset(destination.subtract(original));
            if (!world.isSpaceEmpty(player, box)) return;
        }

        active = true;
        try {
            if (rotate.getInt() == 1) {
                Vec3d aim = target.getBoundingBox().getCenter().subtract(destination.add(0, player.getEyeHeight(), 0));
                float yaw = (float) (Math.toDegrees(Math.atan2(aim.z, aim.x)) - 90.0);
                float pitch = (float) -Math.toDegrees(Math.atan2(aim.y, Math.hypot(aim.x, aim.z)));
                player.networkHandler.sendPacket(new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround(
                        yaw, pitch, player.isOnGround()));
            }
            Tp.send(player, original, destination, step.get(), false);
            player.setPosition(destination);
            mc().interactionManager.attackEntity(player, target);
            player.swingHand(Hand.MAIN_HAND);
            Tp.send(player, destination, original, step.get(), player.isOnGround());
            player.setPosition(original);
            player.setVelocity(Vec3d.ZERO);
        } finally {
            active = false;
            attackTicks = attackDelay.getInt();
        }
    }
}
