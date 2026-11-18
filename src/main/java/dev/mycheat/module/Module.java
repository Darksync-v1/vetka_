package dev.mycheat.module;

import dev.mycheat.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    protected static MinecraftClient mc() { return MinecraftClient.getInstance(); }
    private final String name;
    private final Category category;
    private final String description;
    private boolean enabled;
    private int key = -1;
    public boolean keyDown;
    private final List<NumberSetting> settings = new ArrayList<>();

    protected Module(String name, Category category, String description) {
        this.name = name; this.category = category; this.description = description;
    }

    protected NumberSetting add(NumberSetting s) { settings.add(s); return s; }
    public List<NumberSetting> getSettings() { return settings; }

    public String getName() { return name; }
    public Category getCategory() { return category; }
    public String getDescription() { return description; }
    public boolean isEnabled() { return enabled; }
    public int getKey() { return key; }
    public void setKey(int key) { this.key = key; }

    public void toggle() {
        enabled = !enabled;
        if (mc().player == null) return;
        if (enabled) onEnable(); else onDisable();
        dev.mycheat.util.Notifications.add(name, enabled);
    }

    protected void onEnable() {}
    protected void onDisable() {}
    public void onTick() {}
}
