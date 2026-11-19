package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;

public class Sprint extends Module {
    private final NumberSetting always = add(new NumberSetting("Always", 0, 0, 1, 1));
    private final NumberSetting minFood = add(new NumberSetting("MinFood", 6, 0, 20, 1));

    public Sprint() { super("Sprint", Category.MOVEMENT, "Автоматически включает спринт, пока идёшь вперёд. Always — спринтить даже без зажатого W (если двигаешься стрейфом), MinFood — минимум голода для спринта."); }

    @Override public void onTick() {
        var p = mc().player;
        boolean moving = always.getInt() == 1 ? (p.input.movementForward != 0 || p.input.movementSideways != 0) : p.input.movementForward > 0;
        if (moving && !p.isSneaking() && !p.horizontalCollision && p.getHungerManager().getFoodLevel() > minFood.getInt())
            p.setSprinting(true);
    }
}
