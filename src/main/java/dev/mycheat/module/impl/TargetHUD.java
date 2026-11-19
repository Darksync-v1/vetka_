package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.module.Targets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

/** Compact HUD for the entity currently selected by the combat target system. */
public final class TargetHUD extends Module {
    private static float displayedHealth = -1.0f;
    private static long lastFrame;

    public TargetHUD() {
        super("TargetHUD", Category.RENDER, "Shows the current target name and smoothly animated health bar.");
    }

    public void render(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Entity entity = Targets.get();
        if (mc.player == null || mc.world == null || !(entity instanceof LivingEntity target) || !target.isAlive()) {
            displayedHealth = -1.0f;
            return;
        }

        // Minecraft stores health in points (20 points = 10 hearts).
        // TargetHUD displays hearts, excluding temporary absorption hearts.
        float maxHealth = Math.max(1.0f, target.getMaxHealth());
        float health = MathHelper.clamp(target.getHealth(), 0.0f, maxHealth);
        float ratio = MathHelper.clamp(health / maxHealth, 0.0f, 1.0f);
        // Display health on a 10-heart scale (Minecraft's 20 health points = 10 hearts).
        // Keep the HUD capped at 10 even for entities with boosted maximum health.
        float shownHealth = MathHelper.clamp(health / 2.0f, 0.0f, 10.0f);
        float shownMaxHealth = 10.0f;
        long now = System.currentTimeMillis();
        float delta = lastFrame == 0 ? 1.0f : MathHelper.clamp((now - lastFrame) / 150.0f, 0.0f, 1.0f);
        lastFrame = now;
        if (displayedHealth < 0.0f) displayedHealth = ratio;
        displayedHealth += (ratio - displayedHealth) * delta;

        String name = target instanceof PlayerEntity player
                ? player.getGameProfile().getName()
                : target.getName().getString();
        String hpText = String.format(java.util.Locale.ROOT, "%.1f / %.1f HP", shownHealth, shownMaxHealth);

        int panelWidth = 156;
        int panelHeight = 42;
        int x = (mc.getWindow().getScaledWidth() - panelWidth) / 2;
        int y = mc.getWindow().getScaledHeight() / 2 + 34;

        // Dark, subtle ThunderHack-inspired panel with a violet accent.
        ctx.fill(x, y, x + panelWidth, y + panelHeight, 0xE9161720);
        ctx.fill(x, y, x + 2, y + panelHeight, 0xFF9B75FF);
        ctx.fill(x + 8, y + 8, x + 29, y + 29, 0xFF292B38);
        String initials = name.isEmpty() ? "?" : name.substring(0, 1).toUpperCase(java.util.Locale.ROOT);
        int initialsWidth = mc.textRenderer.getWidth(initials);
        ctx.drawTextWithShadow(mc.textRenderer, initials, x + 18 - initialsWidth / 2, y + 14, 0xFFD8CCFF);

        ctx.drawTextWithShadow(mc.textRenderer, name, x + 36, y + 7, 0xFFFFFFFF);
        int hpWidth = mc.textRenderer.getWidth(hpText);
        ctx.drawTextWithShadow(mc.textRenderer, hpText, x + panelWidth - hpWidth - 7, y + 21, 0xFFB8BBCB);

        int barX = x + 36;
        int barY = y + 33;
        int barWidth = panelWidth - 44;
        ctx.fill(barX, barY, barX + barWidth, barY + 4, 0xFF343643);
        int fillWidth = Math.round(barWidth * displayedHealth);
        int color = ratio > 0.6f ? 0xFF72D99A : ratio > 0.3f ? 0xFFFFC66D : 0xFFFF6D78;
        if (fillWidth > 0) ctx.fill(barX, barY, barX + fillWidth, barY + 4, color);
    }
}
