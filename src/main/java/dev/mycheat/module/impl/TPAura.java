package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.module.Targets;
import dev.mycheat.setting.NumberSetting;
import dev.mycheat.util.Friends;
import dev.mycheat.util.Tp;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

/** Подлетает к цели пакетами, бьёт и возвращается на место. Сервер видит только сам удар и короткий скачок. */
public class TPAura extends Module {
    public static boolean active; // подавляет Criticals на время «телепортного» удара
    private final NumberSetting range = add(new NumberSetting("Range", 30, 5, 60, 1));
    private final NumberSetting step = add(new NumberSetting("Step", 8, 4, 10, 1));

    public TPAura() {
        super("TPAura", Category.COMBAT, "Телепортируется пакетами к игроку в большом радиусе, бьёт и возвращается. Step — длина одного скачка (на строгих серверах ставь меньше).");
    }

    @Override public void onTick() {
        var p = mc().player;
        if (mc().currentScreen != null || p.getAttackCooldownProgress(0.5f) < 0.95f) return;

        Entity target = null;
        double best = range.get() * range.get();
        for (Entity e : mc().world.getEntities()) {
            if (e == p || !(e instanceof PlayerEntity pl) || !pl.isAlive() || pl.isSpectator() || pl.isCreative()
                    || AntiBot.isBot(pl) || Friends.isFriend(pl)) continue;
            double d = e.squaredDistanceTo(p);
            if (d <= best) { best = d; target = e; }
        }
        if (target == null) return;
        Targets.set(target);

        Vec3d from = p.getPos(), t = target.getPos();
        Vec3d dest = from;
        if (from.distanceTo(t) > 3.5) {
            Vec3d d = new Vec3d(from.x - t.x, 0, from.z - t.z);
            d = d.lengthSquared() < 1e-6 ? new Vec3d(1, 0, 0) : d.normalize();
            dest = new Vec3d(t.x + d.x * 1.8, t.y, t.z + d.z * 1.8);
        }
        boolean moved = dest != from;
        active = true;
        try {
            if (moved) Tp.send(p, from, dest, step.get(), false);
            mc().interactionManager.attackEntity(p, target);
            p.swingHand(Hand.MAIN_HAND);
            if (moved) Tp.send(p, dest, from, step.get(), p.isOnGround());
        } finally { active = false; }
    }
}
