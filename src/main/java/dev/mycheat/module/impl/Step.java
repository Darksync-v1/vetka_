package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import net.minecraft.entity.attribute.EntityAttributes;

public class Step extends Module {
    public Step() { super("Step", Category.MOVEMENT, "Позволяет подниматься на блоки высотой до 2 без прыжка."); }
    @Override public void onTick() {
        var a = mc().player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT);
        if (a != null && a.getBaseValue() != 2.0) a.setBaseValue(2.0);
    }
    @Override protected void onDisable() {
        var a = mc().player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT);
        if (a != null) a.setBaseValue(0.6);
    }
}
