package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class AutoTotem extends Module {
    private final NumberSetting hotbarOnly = add(new NumberSetting("HotbarOnly", 0, 0, 1, 1));

    public AutoTotem() { super("AutoTotem", Category.COMBAT, "Если во второй руке нет тотема, берёт его из инвентаря. HotbarOnly — искать только в хотбаре (быстрее отклик)."); }

    @Override public void onTick() {
        var p = mc().player;
        if (mc().currentScreen != null || p.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) return;
        int limit = hotbarOnly.getInt() == 1 ? 9 : 36;
        for (int i = 0; i < limit; i++) {
            if (p.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
                int slot = i < 9 ? i + 36 : i; // hotbar -> screen slot id
                mc().interactionManager.clickSlot(p.playerScreenHandler.syncId, slot, 40, SlotActionType.SWAP, p);
                return;
            }
        }
    }
}
