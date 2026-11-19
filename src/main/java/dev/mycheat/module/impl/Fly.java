package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;

public class Fly extends Module {
    private final NumberSetting speed = add(new NumberSetting("Speed", 0.08, 0.02, 0.4, 0.01));

    public Fly() { super("Fly", Category.MOVEMENT, "Полёт как в креативе, в том числе в режиме выживания. Speed — скорость полёта."); }

    @Override public void onTick() {
        mc().player.getAbilities().allowFlying = true;
        mc().player.getAbilities().flying = true;
        mc().player.getAbilities().setFlySpeed((float) speed.get());
    }
    @Override protected void onDisable() {
        var a = mc().player.getAbilities();
        a.setFlySpeed(0.05f);
        if (!mc().player.isCreative() && !mc().player.isSpectator()) { a.flying = false; a.allowFlying = false; }
    }
}
