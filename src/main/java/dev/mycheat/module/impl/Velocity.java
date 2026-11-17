package dev.mycheat.module.impl;

import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;

/** Уменьшает отбрасывание. Логика в mixin/ClientPlayNetworkHandlerMixin. */
public class Velocity extends Module {
    public final NumberSetting horizontal = add(new NumberSetting("Horizontal %", 0, 0, 100, 5));
    public final NumberSetting vertical = add(new NumberSetting("Vertical %", 0, 0, 100, 5));
    public Velocity() { super("Velocity"); }
}
