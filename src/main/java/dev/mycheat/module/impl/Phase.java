package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;

/** Проход сквозь блоки. noClip включается в ClientPlayerEntityMixin перед каждым шагом движения. */
public class Phase extends Module {
    public Phase() {
        super("Phase", Category.MOVEMENT, "Позволяет проходить сквозь стены, когда упираешься в них. Пробел/Shift — вверх/вниз внутри блоков. Сильно зависит от сервера.");
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
            double vy = p.input.jumping ? 0.2 : p.input.sneaking ? -0.2 : 0;
            p.setVelocity(p.getVelocity().x, vy, p.getVelocity().z);
        }
    }
    @Override protected void onDisable() { mc().player.setNoGravity(false); mc().player.noClip = false; }
}
