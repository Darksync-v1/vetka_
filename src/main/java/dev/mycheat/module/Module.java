package dev.mycheat.module;

import dev.mycheat.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();
    private final String name;
    private boolean enabled;
    private final List<NumberSetting> settings = new ArrayList<>();

    protected Module(String name) { this.name = name; }

    protected NumberSetting add(NumberSetting s) { settings.add(s); return s; }
    public List<NumberSetting> getSettings() { return settings; }

    public String getName() { return name; }
    public boolean isEnabled() { return enabled; }

    public void toggle() {
        enabled = !enabled;
        if (mc.player == null) return;
        if (enabled) onEnable(); else onDisable();
    }

    protected void onEnable() {}
    protected void onDisable() {}
    public void onTick() {}
}
