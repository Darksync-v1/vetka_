package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.util.HudPositions;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;

/** Compact, minimal potion-effects HUD. */
public class PotionHUD extends Module {
    private static final int WIDTH = 118;
    private static final int ROW_HEIGHT = 14;
    private static final int MAX_ROWS = 8;

    public PotionHUD() {
        super("PotionHUD", Category.RENDER, "Компактный список эффектов зелий и времени действия.");
    }

    public void render(DrawContext ctx) {
        if (!isEnabled() || mc().player == null) return;

        int x = HudPositions.x("potions", 14);
        int y = HudPositions.y("potions", 130);
        int rows = 0;

        for (StatusEffectInstance effect : mc().player.getStatusEffects()) {
            if (rows >= MAX_ROWS) break;

            String name = effect.getEffectType().value().getName().getString();
            if (effect.getAmplifier() > 0) name += " " + (effect.getAmplifier() + 1);

            int seconds = Math.max(0, effect.getDuration() / 20);
            String time = String.format(java.util.Locale.ROOT, "%d:%02d", seconds / 60, seconds % 60);

            // Slim dark pill with a restrained violet accent and subtle outline.
            int top = y + rows * ROW_HEIGHT;
            ctx.fill(x, top, x + WIDTH, top + ROW_HEIGHT - 1, 0xB8171923);
            ctx.fill(x, top + 2, x + 2, top + ROW_HEIGHT - 3, 0xFFAA78FF);
            ctx.fill(x + 3, top, x + WIDTH - 1, top + 1, 0x553F3A50);

            int timeWidth = mc().textRenderer.getWidth(time);
            int nameMaxWidth = WIDTH - timeWidth - 17;
            String compactName = mc().textRenderer.trimToWidth(name, nameMaxWidth);
            ctx.drawTextWithShadow(mc().textRenderer, compactName, x + 6, top + 3, 0xFFF2EFF8);
            ctx.drawTextWithShadow(mc().textRenderer, time, x + WIDTH - timeWidth - 5, top + 3, 0xFFD5B8FF);
            rows++;
        }
    }
}
