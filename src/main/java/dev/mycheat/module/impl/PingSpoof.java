package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;

/** Display-only latency offset for HUD testing; it does not alter network packets. */
public class PingSpoof extends Module {
    public final NumberSetting addedPing = add(new NumberSetting("AddedPing", 100, 0, 1000, 10));
    public PingSpoof() { super("PingSpoof", Category.MISC, "Тестовый сдвиг отображаемого пинга в HUD. Не подменяет сетевые пакеты и не меняет реальную задержку."); }
    public static int displayPing(int actual) {
        PingSpoof m = dev.mycheat.MyCheat.MODULES == null ? null : dev.mycheat.MyCheat.MODULES.get(PingSpoof.class);
        return actual + (m != null && m.isEnabled() ? m.addedPing.getInt() : 0);
    }
}
