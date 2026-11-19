package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import net.minecraft.client.gui.DrawContext;

/** Координаты и направление игрока в HUD. */
public class Coordinates extends Module {
    public Coordinates() { super("Coordinates", Category.RENDER, "Показывает XYZ и направление движения."); }
    public void render(DrawContext ctx) {
        if (!isEnabled() || mc().player == null) return;
        var p = mc().player;
        String line = String.format("XYZ: %.1f  %.1f  %.1f", p.getX(), p.getY(), p.getZ());
        String facing = "Facing: " + p.getHorizontalFacing().asString().toUpperCase();
        ctx.fill(5, 5, 150, 29, 0x88090912);
        ctx.drawTextWithShadow(mc().textRenderer, line, 9, 8, 0xFFFFFFFF);
        ctx.drawTextWithShadow(mc().textRenderer, facing, 9, 18, 0xFFB8B8C8);
    }
}
