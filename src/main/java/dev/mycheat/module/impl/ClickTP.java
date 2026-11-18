package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import dev.mycheat.util.Tp;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class ClickTP extends Module {
    private final NumberSetting range = add(new NumberSetting("Range", 60, 10, 120, 1));

    public ClickTP() {
        super("ClickTP", Category.MOVEMENT, "Телепорт на блок, на который смотришь, по нажатию средней кнопки мыши (СКМ). Если смотришь на игрока, СКМ добавляет его в друзья.");
    }

    /** Вызывается из MyCheat по нажатию СКМ. */
    public void teleport() {
        var p = mc().player;
        HitResult hit = p.raycast(range.get(), 1.0f, false);
        if (!(hit instanceof BlockHitResult b) || b.getType() != HitResult.Type.BLOCK) return;
        Vec3d to = Vec3d.ofBottomCenter(b.getBlockPos().offset(b.getSide()));
        Tp.send(p, p.getPos(), to, 8.0, true);
        p.setPosition(to);
        p.setVelocity(Vec3d.ZERO);
    }
}
