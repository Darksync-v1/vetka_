package dev.mycheat.module.impl;

import dev.mycheat.module.Module;

public class Sprint extends Module {
    public Sprint() { super("Sprint"); }
    @Override public void onTick() {
        var p = mc.player;
        if (p.input.movementForward > 0 && !p.isSneaking() && !p.horizontalCollision && p.getHungerManager().getFoodLevel() > 6)
            p.setSprinting(true);
    }
}
