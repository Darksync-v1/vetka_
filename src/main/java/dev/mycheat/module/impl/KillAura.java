package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.module.Targets;
import dev.mycheat.setting.NumberSetting;
import dev.mycheat.util.Friends;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Meteor-inspired KillAura settings adapted to Vetka's lightweight setting system. */
public class KillAura extends Module {
    // General (Meteor-style toggles are represented as 0/1 NumberSettings in this client).
    private final NumberSetting rotation = add(new NumberSetting("Rotation", 1, 0, 2, 1)); // None, Silent, Packet
    private final NumberSetting onlyOnClick = add(new NumberSetting("OnlyOnClick", 0, 0, 1, 1));
    private final NumberSetting onlyOnLook = add(new NumberSetting("OnlyOnLook", 0, 0, 1, 1));
    private final NumberSetting shieldMode = add(new NumberSetting("ShieldMode", 0, 0, 2, 1)); // None, Break, Ignore

    // Targeting.
    private final NumberSetting players = add(new NumberSetting("Players", 1, 0, 1, 1));
    private final NumberSetting hostile = add(new NumberSetting("Hostile", 1, 0, 1, 1));
    private final NumberSetting animals = add(new NumberSetting("Animals", 0, 0, 1, 1));
    private final NumberSetting priority = add(new NumberSetting("Priority", 0, 0, 2, 1)); // Distance, Lowest HP, Highest HP
    private final NumberSetting maxTargets = add(new NumberSetting("MaxTargets", 1, 1, 5, 1));
    private final NumberSetting range = add(new NumberSetting("Range", 5.0, 1.0, 6.0, 0.1));
    private final NumberSetting wallsRange = add(new NumberSetting("WallsRange", 3.0, 0.0, 6.0, 0.1));
    private final NumberSetting ignoreNamed = add(new NumberSetting("IgnoreNamed", 0, 0, 1, 1));

    // Timing.
    private final NumberSetting tpsSync = add(new NumberSetting("TPSSync", 0, 0, 1, 1));
    private final NumberSetting customDelay = add(new NumberSetting("CustomDelay", 0, 0, 1, 1));
    private final NumberSetting hitDelay = add(new NumberSetting("HitDelay", 10, 0, 20, 1));
    private final NumberSetting switchDelay = add(new NumberSetting("SwitchDelay", 0, 0, 20, 1));

    private final List<Entity> targets = new ArrayList<>();
    private Entity current;
    private int hitTimer;
    private int switchTimer;

    public KillAura() {
        super("Aura", Category.COMBAT,
            "KillAura с настройками в стиле Meteor: Rotation, фильтры целей, Range/WallsRange, MaxTargets и Timing. Для переключателей: 0=выкл, 1=вкл.");
    }

    @Override protected void onEnable() {
        hitTimer = 0;
        switchTimer = 0;
        current = null;
        targets.clear();
    }

    @Override protected void onDisable() {
        current = null;
        targets.clear();
        Targets.set(null);
    }

    private boolean valid(Entity entity) {
        if (!(entity instanceof LivingEntity living) || entity == mc().player || !living.isAlive()) return false;
        if (entity instanceof PlayerEntity player) {
            return players.getInt() == 1 && !player.isSpectator() && !player.isCreative()
                && !Friends.isFriend(entity) && !AntiBot.isBot(entity);
        }
        if (entity instanceof HostileEntity) return hostile.getInt() == 1;
        if (entity instanceof AnimalEntity) return animals.getInt() == 1;
        return false;
    }

    private double distanceToBox(Entity e) {
        Vec3d eyes = mc().player.getEyePos();
        var box = e.getBoundingBox();
        double x = MathHelper.clamp(eyes.x, box.minX, box.maxX) - eyes.x;
        double y = MathHelper.clamp(eyes.y, box.minY, box.maxY) - eyes.y;
        double z = MathHelper.clamp(eyes.z, box.minZ, box.maxZ) - eyes.z;
        return Math.sqrt(x * x + y * y + z * z);
    }

    private void updateTargets() {
        targets.clear();
        for (Entity entity : mc().world.getEntities()) {
            if (!valid(entity)) continue;
            double distance = distanceToBox(entity);
            boolean visible = mc().player.canSee(entity);
            double allowed = visible ? range.get() : Math.min(range.get(), wallsRange.get());
            if (distance > allowed) continue;
            if (ignoreNamed.getInt() == 1 && entity.hasCustomName()) continue;
            if (shieldMode.getInt() == 2 && entity instanceof PlayerEntity player && player.isBlocking()) continue;
            if (onlyOnLook.getInt() == 1 && (!(mc().crosshairTarget instanceof EntityHitResult hit) || hit.getEntity() != entity)) continue;
            targets.add(entity);
        }
        Comparator<Entity> comparator = switch (priority.getInt()) {
            case 1 -> Comparator.comparingDouble(e -> ((LivingEntity) e).getHealth());
            case 2 -> Comparator.comparingDouble((Entity e) -> ((LivingEntity) e).getHealth()).reversed();
            default -> Comparator.comparingDouble(this::distanceToBox);
        };
        targets.sort(comparator);
        if (targets.size() > maxTargets.getInt()) targets.subList(maxTargets.getInt(), targets.size()).clear();
        current = targets.isEmpty() ? null : targets.get(0);
        Targets.set(current);
    }

    private void rotateTo(Entity entity) {
        if (rotation.getInt() == 0) return;
        Vec3d eyes = mc().player.getEyePos();
        Vec3d center = entity.getBoundingBox().getCenter();
        double dx = center.x - eyes.x, dy = center.y - eyes.y, dz = center.z - eyes.z;
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.hypot(dx, dz)));
        yaw = MathHelper.wrapDegrees(yaw);
        pitch = MathHelper.clamp(pitch, -90f, 90f);
        // Silent and Packet currently use server-side look packets; camera is not changed.
        mc().player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, mc().player.isOnGround()));
    }

    @Override public void onTick() {
        var player = mc().player;
        if (player == null || mc().world == null || mc().interactionManager == null) return;
        if (hitTimer > 0) hitTimer--;
        if (switchTimer > 0) switchTimer--;
        if (onlyOnClick.getInt() == 1 && !mc().options.attackKey.isPressed()) return;

        updateTargets();
        if (current == null) return;
        if (switchTimer > 0 || hitTimer > 0) return;
        if (player.getAttackCooldownProgress(0.5f) < 0.9f) return;

        rotateTo(current);
        mc().interactionManager.attackEntity(player, current);
        player.swingHand(Hand.MAIN_HAND);

        int delay = customDelay.getInt() == 1 ? hitDelay.getInt() : 0;
        if (tpsSync.getInt() == 1) delay = Math.max(delay, 1);
        hitTimer = delay;
        switchTimer = switchDelay.getInt();
    }
}
