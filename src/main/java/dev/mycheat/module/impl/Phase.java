package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;

/** Проход сквозь блоки. noClip включается в ClientPlayerEntityMixin перед каждым шагом движения. */
public class Phase extends Module {
    private final NumberSetting vspeed = add(new NumberSetting("VSpeed", 0.2, 0.05, 0.5, 0.05));

    public Phase() {
        super("Phase", Category.MOVEMENT, "Позволяет проходить сквозь стены, когда упираешься в них. Пробел/Shift — вверх/вниз внутри блоков. VSpeed — скорость этого движения. Сильно зависит от сервера.");
    }

    private boolean inside() {
        return mc().world.getBlockCollisions(mc().player, mc().player.getBoundingBox()).iterator().hasNext();
    }

    public boolean shouldClip() {
        return mc().player.horizontalCollision || inside();
    }

    @Override public void onTick() {
        var p = mc().player;
        boolean clip = p.horizontalCollision || inside();
        p.setNoGravity(clip);
        if (clip) {
            double vy = p.input.jumping ? vspeed.get() : p.input.sneaking ? -vspeed.get() : 0;
            p.setVelocity(p.getVelocity().x, vy, p.getVelocity().z);
        }
    }
    @Override protected void onDisable() { mc().player.setNoGravity(false); mc().player.noClip = false; }
}
