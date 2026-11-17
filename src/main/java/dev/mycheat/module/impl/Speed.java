package dev.mycheat.module.impl;

import dev.mycheat.module.Module;
import net.minecraft.util.math.Vec3d;

public class Speed extends Module {
    private static final double SPEED = 0.32;
    public Speed() { super("Speed"); }
    @Override public void onTick() {
        var p = mc.player;
        float f = p.input.movementForward, s = p.input.movementSideways;
        if ((f == 0 && s == 0) || !p.isOnGround()) return;
        double yaw = Math.toRadians(p.getYaw());
        double sin = Math.sin(yaw), cos = Math.cos(yaw);
        double x = (s * cos - f * sin), z = (f * cos + s * sin);
        double len = Math.sqrt(x * x + z * z);
        Vec3d v = p.getVelocity();
        p.setVelocity(x / len * SPEED, v.y, z / len * SPEED);
    }
}
