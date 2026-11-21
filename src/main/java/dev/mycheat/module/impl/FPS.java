package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.util.HudPositions;
import net.minecraft.client.gui.DrawContext;

/** FPS counter in the HUD. */
public class FPS extends Module {
    public FPS() { super("FPS", Category.RENDER, "Показывает текущий FPS."); }
    public void render(DrawContext ctx) {
        if (!isEnabled()) return;
        String value = "FPS: " + mc().getCurrentFps();
        int x=HudPositions.x("fps",5), y=HudPositions.y("fps",33);
        ctx.fill(x,y,x+70,y+16,0xCC171923);
        ctx.drawTextWithShadow(mc().textRenderer,value,x+4,y+4,0xFF80E8FF);
    }
}
