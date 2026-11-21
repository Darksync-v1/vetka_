package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.util.HudPositions;
import net.minecraft.client.gui.DrawContext;

/** Координаты и направление игрока в HUD. */
public class Coordinates extends Module {
    public Coordinates() { super("Coordinates", Category.RENDER, "Показывает XYZ и направление движения."); }
    public void render(DrawContext ctx) {
        if (!isEnabled() || mc().player == null) return;
        var p = mc().player;
        String line = String.format("XYZ: %.1f  %.1f  %.1f", p.getX(), p.getY(), p.getZ());
        String facing = "Facing: " + p.getHorizontalFacing().asString().toUpperCase();
        int x=HudPositions.x("coordinates",5), y=HudPositions.y("coordinates",5);
        ctx.fill(x, y, x+150, y+25, 0xCC171923);
        ctx.drawTextWithShadow(mc().textRenderer, line, x+4, y+3, 0xFFFFFFFF);
        ctx.drawTextWithShadow(mc().textRenderer, facing, x+4, y+14, 0xFFB8B8C8);
    }
}
