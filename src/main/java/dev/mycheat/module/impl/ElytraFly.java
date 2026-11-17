package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.util.math.Vec3d;

/** Полёт на элитрах без фейерверков: W летит по взгляду, без клавиш зависаешь, Space/Shift вверх/вниз. */
public class ElytraFly extends Module {
    private final NumberSetting speed = add(new NumberSetting("Speed", 1.5, 0.5, 4.0, 0.1));
    private final NumberSetting vspeed = add(new NumberSetting("Vertical", 0.8, 0.2, 2.0, 0.1));

    public ElytraFly() { super("ElytraFly", Category.MOVEMENT, "Полёт на элитрах без фейерверков: W — вперёд по взгляду, Пробел и Shift — вверх и вниз. Элитры раскрой сам."); }

    @Override public void onTick() {
        var p = mc.player;
        if (!p.isFallFlying()) return; // элитры нужно раскрыть обычным способом: прыжок в воздухе
        Vec3d look = p.getRotationVec(1.0f);
        Vec3d v = Vec3d.ZERO;
        if (p.input.movementForward > 0) v = look.multiply(speed.get());
        else if (p.input.movementForward < 0) v = look.multiply(-speed.get());
        double y = v.y;
        if (p.input.jumping) y = vspeed.get();
        else if (p.input.sneaking) y = -vspeed.get();
        p.setVelocity(v.x, y, v.z);
    }
}
