package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import net.minecraft.client.gui.DrawContext;

/** FPS counter in the HUD. */
public class FPS extends Module {
    public FPS() { super("FPS", Category.RENDER, "Показывает текущий FPS."); }
    public void render(DrawContext ctx) {
        if (!isEnabled()) return;
        String value = "FPS: " + mc().getCurrentFps();
        ctx.fill(5, 33, 65, 46, 0x88090912);
        ctx.drawTextWithShadow(mc().textRenderer, value, 9, 36, 0xFF80E8FF);
    }
}
