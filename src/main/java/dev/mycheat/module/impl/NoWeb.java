package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/** Игнорирует замедление в паутине: держит обычную скорость, пока стоишь в блоке паутины. */
public class NoWeb extends Module {
    private final NumberSetting speed = add(new NumberSetting("Speed", 0.215, 0.1, 0.3, 0.005));

    public NoWeb() {
        super("NoWeb", Category.MOVEMENT, "Игнорирует замедление от паутины: ходишь и бегаешь в ней с обычной скоростью.");
    }

    @Override public void onTick() {
        var p = mc.player;
        BlockPos pos = BlockPos.ofFloored(p.getX(), p.getY(), p.getZ());
        if (mc.world.getBlockState(pos).isOf(Blocks.COBWEB)) {
            float f = p.input.movementForward, s = p.input.movementSideways;
            if (f == 0 && s == 0) return;
            double yaw = Math.toRadians(p.getYaw());
            double sin = Math.sin(yaw), cos = Math.cos(yaw);
            double x = s * cos - f * sin, z = f * cos + s * sin, len = Math.hypot(x, z);
            Vec3d v = p.getVelocity();
            p.setVelocity(x / len * speed.get(), v.y, z / len * speed.get());
        }
    }
}
