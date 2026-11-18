package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;

public class Fly extends Module {
    public Fly() { super("Fly", Category.MOVEMENT, "Полёт как в креативе, в том числе в режиме выживания."); }
    @Override public void onTick() {
        mc().player.getAbilities().allowFlying = true;
        mc().player.getAbilities().flying = true;
        mc().player.getAbilities().setFlySpeed(0.08f);
    }
    @Override protected void onDisable() {
        var a = mc().player.getAbilities();
        a.setFlySpeed(0.05f);
        if (!mc().player.isCreative() && !mc().player.isSpectator()) { a.flying = false; a.allowFlying = false; }
    }
}
