package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.util.HudPositions;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;

/** Показывает над хотбаром броню и прочность предметов. */
public class ArmorStatus extends Module {
    public ArmorStatus() { super("ArmorStatus", Category.RENDER, "Показывает броню и оставшуюся прочность."); }
    public void render(DrawContext ctx) {
        if (!isEnabled() || mc().player == null) return;
        var p = mc().player;
        int x = HudPositions.x("armor",mc().getWindow().getScaledWidth()/2-74);
        int y = HudPositions.y("armor",mc().getWindow().getScaledHeight()-58);
        EquipmentSlot[] slots = {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};
        for (int i = 0; i < slots.length; i++) {
            var stack = p.getEquippedStack(slots[i]);
            int ix = x + i * 18;
            if (!stack.isEmpty()) {
                ctx.drawItem(stack, ix, y);
                if (stack.isDamageable()) {
                    int left = stack.getMaxDamage() - stack.getDamage();
                    int pct = Math.round(left * 100f / stack.getMaxDamage());
                    int color = pct > 60 ? 0xFF55FF55 : pct > 25 ? 0xFFFFFF55 : 0xFFFF5555;
                    String text = pct + "%";
                    ctx.drawTextWithShadow(mc().textRenderer, text, ix + 1, y - 9, color);
                }
            }
        }
    }
}
