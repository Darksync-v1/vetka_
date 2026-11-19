package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.entity.attribute.EntityAttributes;

public class Step extends Module {
    private final NumberSetting height = add(new NumberSetting("Height", 1.0, 0.6, 2.0, 0.1));

    public Step() { super("Step", Category.MOVEMENT, "Позволяет подниматься на блоки без прыжка. Height — максимальная высота подъёма."); }

    @Override public void onTick() {
        var a = mc().player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT);
        if (a != null) a.setBaseValue(height.get());
    }
    @Override protected void onDisable() {
        var a = mc().player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT);
        if (a != null) a.setBaseValue(0.6);
    }
}
