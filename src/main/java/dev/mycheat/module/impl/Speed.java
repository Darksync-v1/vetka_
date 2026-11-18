package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.util.math.Vec3d;

public class Speed extends Module {
    private final NumberSetting mode = add(new NumberSetting("Mode", 0, 0, 1, 1));
    private final NumberSetting speed = add(new NumberSetting("Speed", 0.30, 0.15, 0.6, 0.01));
    private double cur;

    public Speed() {
        super("Speed", Category.MOVEMENT, "Ускорение. Mode 0 — банихоп: прыжок и скорость, затухающая в воздухе как у ванили. Mode 1 — быстрый бег только по земле. Для NCP держи Speed около 0.28-0.32.");
    }

    @Override public void onTick() {
        var p = mc().player;
        float f = p.input.movementForward, s = p.input.movementSideways;
        if ((f == 0 && s == 0) || p.isTouchingWater() || p.isInLava() || p.isFallFlying()
                || p.isClimbing() || p.getAbilities().flying) { cur = 0; return; }

        if (p.isOnGround()) {
            if (mode.getInt() == 0) { p.jump(); cur = speed.get(); }
            else cur = speed.get() * 0.8;
        } else if (mode.getInt() == 0) {
            cur = Math.max(0.2, cur * 0.985); // затухание в воздухе
        } else return;

        double yaw = Math.toRadians(p.getYaw());
        double sin = Math.sin(yaw), cos = Math.cos(yaw);
        double x = s * cos - f * sin, z = f * cos + s * sin;
        double len = Math.hypot(x, z);
        if (len == 0) return;
        Vec3d v = p.getVelocity();
        p.setVelocity(x / len * cur, v.y, z / len * cur);
    }
}
