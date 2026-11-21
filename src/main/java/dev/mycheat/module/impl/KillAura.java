package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.module.Targets;
import dev.mycheat.setting.NumberSetting;
import dev.mycheat.util.Friends;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * ThunderHack Recode Aura-inspired port for Vetka's lightweight module/settings API.
 * The original Aura depends on ThunderHack's event, rotation, inventory and raytrace managers;
 * this implementation recreates the portable target/rotation/attack flow without those dependencies.
 */
public class KillAura extends Module {
    // Main Aura settings (names and modes follow ThunderHack Recode where practical).
    private final NumberSetting attackRange = add(new NumberSetting("Range", 3.6, 1.0, 6.0, 0.1));
    private final NumberSetting wallRange = add(new NumberSetting("ThroughWallsRange", 3.6, 0.0, 6.0, 0.1));
    private final NumberSetting elytraOverride = add(new NumberSetting("ElytraOverride", 0, 0, 1, 1));
    private final NumberSetting elytraAttackRange = add(new NumberSetting("ElytraRange", 3.1, 1.0, 6.0, 0.1));
    private final NumberSetting elytraWallRange = add(new NumberSetting("ElytraThroughWallsRange", 3.1, 0.0, 6.0, 0.1));
    private final NumberSetting fov = add(new NumberSetting("FOV", 180, 1, 180, 1));
    // RotationMode: 0=Track, 1=Grim(packet), 2=Interact(track), 3=None.
    private final NumberSetting rotationMode = add(new NumberSetting("RotationMode", 0, 0, 3, 1));
    private final NumberSetting autoAim = add(new NumberSetting("AutoAim", 1, 0, 1, 1));
    private final NumberSetting aimSpeed = add(new NumberSetting("AimSpeed", 70, 5, 180, 1));
    private final NumberSetting interactTicks = add(new NumberSetting("InteractTicks", 3, 1, 10, 1));
    // AutoWeapon: 0=None, 1=Normal, 2=Silent (hotbar-only packet switch).
    private final NumberSetting autoWeapon = add(new NumberSetting("AutoWeapon", 0, 0, 2, 1));
    private final NumberSetting onlyWeapon = add(new NumberSetting("OnlyWeapon", 0, 0, 1, 1));
    private final NumberSetting smartCrit = add(new NumberSetting("SmartCrit", 1, 0, 1, 1));
    private final NumberSetting onlyCrit = add(new NumberSetting("OnlyCrit", 0, 0, 1, 1));
    private final NumberSetting autoJump = add(new NumberSetting("AutoJump", 0, 0, 1, 1));
    private final NumberSetting shieldBreaker = add(new NumberSetting("ShieldBreaker", 1, 0, 1, 1));
    private final NumberSetting pauseWhileEating = add(new NumberSetting("PauseWhileEating", 0, 0, 1, 1));
    private final NumberSetting clientLook = add(new NumberSetting("ClientLook", 0, 0, 1, 1));
    private final NumberSetting oldDelay = add(new NumberSetting("OldDelay", 0, 0, 1, 1));
    private final NumberSetting minCPS = add(new NumberSetting("MinCPS", 7, 1, 20, 1));
    private final NumberSetting maxCPS = add(new NumberSetting("MaxCPS", 12, 1, 20, 1));

    // Target selection.
    private final NumberSetting sort = add(new NumberSetting("Sort", 0, 0, 4, 1)); // distance, farthest, low/high HP, FOV
    private final NumberSetting lockTarget = add(new NumberSetting("LockTarget", 1, 0, 1, 1));
    private final NumberSetting elytraTarget = add(new NumberSetting("ElytraTarget", 1, 0, 1, 1));
    private final NumberSetting players = add(new NumberSetting("Players", 1, 0, 1, 1));
    private final NumberSetting mobs = add(new NumberSetting("Mobs", 1, 0, 1, 1));
    private final NumberSetting animals = add(new NumberSetting("Animals", 1, 0, 1, 1));
    private final NumberSetting villagers = add(new NumberSetting("Villagers", 1, 0, 1, 1));
    private final NumberSetting slimes = add(new NumberSetting("Slimes", 1, 0, 1, 1));
    private final NumberSetting hostiles = add(new NumberSetting("Hostiles", 1, 0, 1, 1));
    private final NumberSetting ignoreInvisible = add(new NumberSetting("IgnoreInvisibleEntities", 0, 0, 1, 1));
    private final NumberSetting ignoreNamed = add(new NumberSetting("IgnoreNamed", 0, 0, 1, 1));
    private final NumberSetting ignoreTeam = add(new NumberSetting("IgnoreTeam", 0, 0, 1, 1));
    private final NumberSetting ignoreCreative = add(new NumberSetting("IgnoreCreative", 1, 0, 1, 1));
    private final NumberSetting ignoreNaked = add(new NumberSetting("IgnoreNaked", 0, 0, 1, 1));
    private final NumberSetting attackShieldingEntities = add(new NumberSetting("AttackShieldingEntities", 1, 0, 1, 1));

    // Advanced timing / movement / ray checks.
    private final NumberSetting aimRange = add(new NumberSetting("AimRange", 0.0, 0.0, 6.0, 0.1));
    private final NumberSetting randomHitDelay = add(new NumberSetting("RandomHitDelay", 0, 0, 1, 1));
    private final NumberSetting pauseInInventory = add(new NumberSetting("PauseInInventory", 1, 0, 1, 1));
    private final NumberSetting dropSprint = add(new NumberSetting("DropSprint", 1, 0, 1, 1));
    private final NumberSetting returnSprint = add(new NumberSetting("ReturnSprint", 1, 0, 1, 1));
    private final NumberSetting rayTrace = add(new NumberSetting("RayTrace", 1, 0, 2, 1)); // 0=Off, 1=OnlyTarget, 2=AllEntities
    private final NumberSetting deathDisable = add(new NumberSetting("DisableOnDeath", 1, 0, 1, 1));
    private final NumberSetting fastFall = add(new NumberSetting("FastFall", 0, 0, 1, 1));
    private final NumberSetting pullValue = add(new NumberSetting("PullValue", 3, 0, 20, 0.5));
    private final NumberSetting attackHand = add(new NumberSetting("AttackHand", 0, 0, 1, 1)); // 0=main, 1=off
    private final NumberSetting attackCooldown = add(new NumberSetting("AttackCooldown", 0.9, 0.5, 1.0, 0.01));
    private final NumberSetting attackTickLimit = add(new NumberSetting("AttackTickLimit", 11, 0, 20, 1));
    private final NumberSetting critFallDistance = add(new NumberSetting("CritFallDistance", 0, 0, 1, 0.05));

    private Entity target;
    private int hitTicks;
    private int interactTicksLeft;
    private float rotationYaw, rotationPitch;

    public KillAura() {
        super("Aura", Category.COMBAT,
                "ThunderHack Recode Aura port. RotationMode: 0=Track, 1=Grim, 2=Interact, 3=None; AutoWeapon: 0=None, 1=Normal, 2=Silent; Sort: 0=Nearest, 1=Farthest, 2=LowestHP, 3=HighestHP, 4=FOV; RayTrace: 0=Off, 1=OnlyTarget, 2=AllEntities (both non-Off modes use visibility checks in this lightweight port).");
    }

    @Override protected void onEnable() {
        target = null;
        hitTicks = 0;
        interactTicksLeft = 0;
        if (mc().player != null) {
            rotationYaw = mc().player.getYaw();
            rotationPitch = mc().player.getPitch();
        }
        Targets.set(null);
    }

    @Override protected void onDisable() {
        target = null;
        Targets.set(null);
    }

    private double range() {
        return elytraOverride.getInt() == 1 && mc().player.isFallFlying() ? elytraAttackRange.get() : attackRange.get();
    }

    private double wallRange() {
        return elytraOverride.getInt() == 1 && mc().player.isFallFlying() ? elytraWallRange.get() : wallRange.get();
    }

    private double distanceToBox(Entity entity) {
        Vec3d eyes = mc().player.getEyePos();
        var box = entity.getBoundingBox();
        double x = MathHelper.clamp(eyes.x, box.minX, box.maxX) - eyes.x;
        double y = MathHelper.clamp(eyes.y, box.minY, box.maxY) - eyes.y;
        double z = MathHelper.clamp(eyes.z, box.minZ, box.maxZ) - eyes.z;
        return Math.sqrt(x * x + y * y + z * z);
    }

    private float fovAngle(Entity entity) {
        double dx = entity.getX() - mc().player.getX();
        double dz = entity.getZ() - mc().player.getZ();
        float wanted = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        return Math.abs(MathHelper.wrapDegrees(wanted - mc().player.getYaw()));
    }

    private boolean selectedType(Entity entity) {
        if (entity instanceof PlayerEntity) return players.getInt() == 1;
        if (entity instanceof VillagerEntity) return villagers.getInt() == 1;
        if (entity instanceof SlimeEntity) return slimes.getInt() == 1;
        if (entity instanceof HostileEntity) return hostiles.getInt() == 1;
        if (entity instanceof AnimalEntity) return animals.getInt() == 1;
        if (entity instanceof MobEntity) return mobs.getInt() == 1;
        return false;
    }

    private boolean valid(Entity entity) {
        if (!(entity instanceof LivingEntity living) || entity == mc().player || !living.isAlive() || !selectedType(entity)) return false;
        if (living.isInvisible() && ignoreInvisible.getInt() == 1) return false;
        if (ignoreNamed.getInt() == 1 && entity.hasCustomName()) return false;
        if (entity instanceof PlayerEntity player) {
            if (player.isSpectator() || (ignoreCreative.getInt() == 1 && player.isCreative())) return false;
            if (Friends.isFriend(player) || AntiBot.isBot(player)) return false;
            if (ignoreNaked.getInt() == 1 && player.getArmor() == 0) return false;
            if (ignoreTeam.getInt() == 1 && mc().player.getScoreboardTeam() != null
                    && mc().player.getScoreboardTeam() == player.getScoreboardTeam()) return false;
            if (attackShieldingEntities.getInt() == 0 && player.isBlocking()) return false;
        }
        double dist = distanceToBox(entity);
        double managedRange = TargetManager.maxRange(range());
        double allowed = mc().player.canSee(entity) ? managedRange : Math.min(wallRange(), managedRange);
        if (dist > allowed + aimRange.get()) return false;
        return fovAngle(entity) <= fov.get();
    }

    private Entity findTarget() {
        List<Entity> candidates = new ArrayList<>();
        for (Entity entity : mc().world.getEntities()) if (valid(entity)) candidates.add(entity);
        int managedPriority = TargetManager.priority();
        int selectedSort = managedPriority >= 0 ? (managedPriority == 1 ? 2 : managedPriority == 2 ? 3 : 0) : sort.getInt();
        Comparator<Entity> comparator = switch (selectedSort) {
            case 1 -> Comparator.comparingDouble(this::distanceToBox).reversed();
            case 2 -> Comparator.comparingDouble(e -> ((LivingEntity) e).getHealth() + ((LivingEntity) e).getAbsorptionAmount());
            case 3 -> Comparator.comparingDouble((Entity e) -> ((LivingEntity) e).getHealth() + ((LivingEntity) e).getAbsorptionAmount()).reversed();
            case 4 -> Comparator.comparingDouble(this::fovAngle);
            default -> Comparator.comparingDouble(this::distanceToBox);
        };
        candidates.sort(comparator);
        return candidates.isEmpty() ? null : candidates.get(0);
    }

    private boolean hasWeapon() {
        if (onlyWeapon.getInt() == 0) return true;
        Item item = mc().player.getMainHandStack().getItem();
        return item instanceof SwordItem || item instanceof AxeItem || item instanceof TridentItem
                || (autoWeapon.getInt() != 0 && findWeaponSlot() >= 0);
    }

    private int findWeaponSlot() {
        for (int i = 0; i < 9; i++) {
            Item item = mc().player.getInventory().getStack(i).getItem();
            if (item instanceof SwordItem || item instanceof AxeItem || item instanceof TridentItem) return i;
        }
        return -1;
    }

    private void rotateTo(Entity entity) {
        if (autoAim.getInt() == 0 || rotationMode.getInt() == 3) return;
        Vec3d eyes = mc().player.getEyePos();
        Vec3d point = entity.getBoundingBox().getCenter();
        double dx = point.x - eyes.x, dy = point.y - eyes.y, dz = point.z - eyes.z;
        float wantedYaw = MathHelper.wrapDegrees((float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0));
        float wantedPitch = MathHelper.clamp((float) -Math.toDegrees(Math.atan2(dy, Math.hypot(dx, dz))), -90, 90);
        float yawStep = rotationMode.getInt() == 0 ? (float) aimSpeed.get() : 180f;
        float pitchStep = rotationMode.getInt() == 0 ? Math.max(2f, (float) aimSpeed.get() * 0.18f) : 90f;
        rotationYaw += MathHelper.clamp(MathHelper.wrapDegrees(wantedYaw - rotationYaw), -yawStep, yawStep);
        rotationPitch += MathHelper.clamp(wantedPitch - rotationPitch, -pitchStep, pitchStep);
        if (clientLook.getInt() == 1 || rotationMode.getInt() == 0 || rotationMode.getInt() == 2) {
            mc().player.setYaw(rotationYaw);
            mc().player.setPitch(rotationPitch);
        }
    }

    private boolean criticalReady() {
        if (smartCrit.getInt() == 0) return true;
        var p = mc().player;
        if (p.isInLava() || p.isSubmergedInWater() || p.isFallFlying() || p.getAbilities().flying) return true;
        if (onlyCrit.getInt() == 1 && p.isOnGround() && autoJump.getInt() == 0) return false;
        if (p.isOnGround() && autoJump.getInt() == 1 && !mc().options.jumpKey.isPressed()) p.jump();
        if (p.isOnGround() && onlyCrit.getInt() == 1) return false;
        return p.isOnGround() || p.fallDistance > critFallDistance.get();
    }

    private int chooseWeapon() {
        if (autoWeapon.getInt() == 0) return -1;
        int slot = findWeaponSlot();
        if (slot < 0 || slot == mc().player.getInventory().selectedSlot) return -1;
        int previous = mc().player.getInventory().selectedSlot;
        if (autoWeapon.getInt() == 1) {
            mc().player.getInventory().selectedSlot = slot;
            mc().player.networkHandler.sendPacket(new net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket(slot));
        } else {
            mc().player.networkHandler.sendPacket(new net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket(slot));
        }
        return previous;
    }

    private void restoreWeapon(int previous) {
        if (previous < 0) return;
        if (autoWeapon.getInt() == 1) mc().player.getInventory().selectedSlot = previous;
        mc().player.networkHandler.sendPacket(new net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket(previous));
    }

    private void doAttack() {
        var player = mc().player;
        boolean sprinting = player.isSprinting();
        if (sprinting && dropSprint.getInt() == 1) {
            player.setSprinting(false);
            player.networkHandler.sendPacket(new net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket(player,
                    net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode.STOP_SPRINTING));
        }
        int previousSlot = chooseWeapon();
        Hand hand = attackHand.getInt() == 1 ? Hand.OFF_HAND : Hand.MAIN_HAND;
        if (rotationMode.getInt() == 1) {
            player.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(player.getX(), player.getY(), player.getZ(), rotationYaw, rotationPitch, player.isOnGround()));
        }
        mc().interactionManager.attackEntity(player, target);
        player.swingHand(hand);
        restoreWeapon(previousSlot);
        if (sprinting && dropSprint.getInt() == 1 && returnSprint.getInt() == 1) {
            player.setSprinting(true);
            player.networkHandler.sendPacket(new net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket(player,
                    net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode.START_SPRINTING));
        }
        if (rotationMode.getInt() == 1) {
            player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(player.getYaw(), player.getPitch(), player.isOnGround()));
        }
        int delay;
        if (oldDelay.getInt() == 1) {
            int min = Math.min(minCPS.getInt(), maxCPS.getInt());
            int max = Math.max(minCPS.getInt(), maxCPS.getInt());
            int cps = min + (int) (Math.random() * (max - min + 1));
            delay = Math.max(1, 20 / Math.max(1, cps));
        } else {
            delay = attackTickLimit.getInt();
            if (randomHitDelay.getInt() == 1) delay = 10 + (int) (Math.random() * 4);
        }
        hitTicks = delay;
    }

    @Override public void onTick() {
        var player = mc().player;
        if (player == null || mc().world == null || mc().interactionManager == null || player.networkHandler == null) return;
        if (deathDisable.getInt() == 1 && player.isDead()) { toggle(); return; }
        if (hitTicks > 0) hitTicks--;
        if (pauseWhileEating.getInt() == 1 && player.isUsingItem()) return;
        if (pauseInInventory.getInt() == 1 && mc().currentScreen != null) return;
        if (!hasWeapon()) { target = null; Targets.set(null); return; }

        Entity candidate = findTarget();
        if (target == null || !valid(target) || lockTarget.getInt() == 0 || sort.getInt() == 4) target = candidate;
        if (candidate != null && (target == null || !valid(target))) target = candidate;
        if (target == null) { Targets.set(null); return; }
        if (player.isFallFlying() && elytraTarget.getInt() == 0) return;
        Targets.set(target);

        if (autoWeapon.getInt() != 0 && onlyWeapon.getInt() == 1 && findWeaponSlot() < 0) return;
        rotateTo(target);
        if (rotationMode.getInt() == 2) {
            if (interactTicksLeft++ < interactTicks.getInt()) return;
            interactTicksLeft = 0;
        }
        if (hitTicks > 0 || player.getAttackCooldownProgress(0.5f) < attackCooldown.get()) return;
        double managedRange = TargetManager.maxRange(range());
        if (distanceToBox(target) > (player.canSee(target) ? managedRange : Math.min(wallRange(), managedRange))) return;
        if (!criticalReady()) return;
        if (fastFall.getInt() == 1 && !player.isOnGround()) player.addVelocity(0.0, -pullValue.get() / 1000.0, 0.0);
        if (rayTrace.getInt() != 0 && !player.canSee(target) && distanceToBox(target) > wallRange()) return;
        if (shieldBreaker.getInt() == 1 && target instanceof PlayerEntity other && other.isBlocking()) {
            int axe = -1;
            for (int i = 0; i < 9; i++) if (player.getInventory().getStack(i).getItem() instanceof AxeItem) { axe = i; break; }
            if (axe >= 0) {
                int old = player.getInventory().selectedSlot;
                player.getInventory().selectedSlot = axe;
                player.networkHandler.sendPacket(new net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket(axe));
                mc().interactionManager.attackEntity(player, target);
                player.swingHand(Hand.MAIN_HAND);
                player.getInventory().selectedSlot = old;
                player.networkHandler.sendPacket(new net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket(old));
                hitTicks = 10;
                return;
            }
        }
        doAttack();
    }
}
