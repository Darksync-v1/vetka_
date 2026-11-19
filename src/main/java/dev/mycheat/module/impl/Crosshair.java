package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.client.gui.DrawContext;

/** Настраиваемый HUD-прицел поверх стандартного прицела. */
public class Crosshair extends Module {
    private final NumberSetting size = add(new NumberSetting("Size", 5, 2, 14, 1));
    private final NumberSetting gap = add(new NumberSetting("Gap", 3, 0, 10, 1));
    private final NumberSetting thickness = add(new NumberSetting("Thickness", 1, 1, 4, 1));
    public Crosshair() { super("Crosshair", Category.RENDER, "Настраиваемый центральный прицел."); }
    public void render(DrawContext ctx) {
        if (!isEnabled()) return;
        int cx = mc().getWindow().getScaledWidth() / 2;
        int cy = mc().getWindow().getScaledHeight() / 2;
        int s = size.getInt(), g = gap.getInt(), t = thickness.getInt();
        int color = 0xEFFFFFFF;
        ctx.fill(cx - s - g, cy - t / 2, cx - g, cy - t / 2 + t, color);
        ctx.fill(cx + g + 1, cy - t / 2, cx + g + 1 + s, cy - t / 2 + t, color);
        ctx.fill(cx - t / 2, cy - s - g, cx - t / 2 + t, cy - g, color);
        ctx.fill(cx - t / 2, cy + g + 1, cx - t / 2 + t, cy + g + 1 + s, color);
    }
}
