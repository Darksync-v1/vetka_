package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;

public class Sprint extends Module {
    public Sprint() { super("Sprint", Category.MOVEMENT, "Автоматически включает спринт, пока идёшь вперёд."); }
    @Override public void onTick() {
        var p = mc.player;
        if (p.input.movementForward > 0 && !p.isSneaking() && !p.horizontalCollision && p.getHungerManager().getFoodLevel() > 6)
            p.setSprinting(true);
    }
}
