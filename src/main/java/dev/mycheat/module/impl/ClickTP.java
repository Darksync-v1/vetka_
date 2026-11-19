package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import dev.mycheat.util.Tp;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class ClickTP extends Module {
    private final NumberSetting range = add(new NumberSetting("Range", 256, 16, 256, 1));
    private int cooldown;

    public ClickTP() {
        super("ClickTP", Category.MOVEMENT, "Телепорт на блок, на который смотришь, по нажатию средней кнопки мыши (СКМ). Если смотришь на игрока, СКМ добавляет его в друзья.");
    }

    @Override public void onTick() { if (cooldown > 0) cooldown--; }

    /** Вызывается из MyCheat по нажатию СКМ. */
    public void teleport() {
        var p = mc().player;
        if (p == null || mc().world == null || p.networkHandler == null || cooldown > 0) return;
        HitResult hit = p.raycast(range.get(), 1.0f, false);
        if (!(hit instanceof BlockHitResult b) || b.getType() != HitResult.Type.BLOCK) return;

        // Mimics Vinted's ClickTP: aim at a solid block, then choose a free position
        // at/above it rather than placing the player inside a solid block.
        var pos = b.getBlockPos();
        for (int i = 0; i < 256 && mc().world.getBlockState(pos).getCollisionShape(mc().world, pos).isEmpty(); i++) {
            pos = pos.down();
        }
        if (mc().world.getBlockState(pos).getCollisionShape(mc().world, pos).isEmpty()) return;
        var destination = pos.up();
        for (int i = 0; i < 256; i++) {
            var feet = mc().world.getBlockState(destination).getCollisionShape(mc().world, destination).isEmpty();
            var headPos = destination.up();
            var head = mc().world.getBlockState(headPos).getCollisionShape(mc().world, headPos).isEmpty();
            if (feet && head) break;
            destination = destination.up();
        }
        Vec3d to = Vec3d.ofBottomCenter(destination);
        Vec3d from = p.getPos();
        Tp.send(p, from, to, 11.0, true);
        p.setPosition(to);
        p.setVelocity(Vec3d.ZERO);
        cooldown = 5;
    }
}
