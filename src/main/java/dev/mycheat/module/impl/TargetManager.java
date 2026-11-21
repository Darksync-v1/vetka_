package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;

/** Shared target-selection preferences consumed by KillAura. */
public class TargetManager extends Module {
    public final NumberSetting priority = add(new NumberSetting("Priority (0 Distance, 1 LowestHP, 2 HighestHP)", 0, 0, 2, 1));
    public final NumberSetting maxRange = add(new NumberSetting("MaxRange", 3.6, 1, 6, 0.1));
    public TargetManager() { super("TargetManager", Category.COMBAT, "Приоритет выбора цели и максимальная дистанция для Aura."); }
    public static int priority() {
        TargetManager m = dev.mycheat.MyCheat.MODULES == null ? null : dev.mycheat.MyCheat.MODULES.get(TargetManager.class);
        return m != null && m.isEnabled() ? m.priority.getInt() : -1;
    }
    public static double maxRange(double fallback) {
        TargetManager m = dev.mycheat.MyCheat.MODULES == null ? null : dev.mycheat.MyCheat.MODULES.get(TargetManager.class);
        return m != null && m.isEnabled() ? Math.min(fallback, m.maxRange.get()) : fallback;
    }
}
