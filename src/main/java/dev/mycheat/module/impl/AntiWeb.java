package dev.mycheat.module.impl;

import dev.mycheat.MyCheat;
import dev.mycheat.module.Category;
import dev.mycheat.module.Module;

/**
 * Делает паутину полностью твёрдой: зайти внутрь неё нельзя, как будто это обычный блок.
 * Это противоположность NoWeb (который даёт ходить сквозь паутину без замедления) — используй
 * то, что нужно именно тебе. Коллизия меняется в CobwebMixin.
 */
public class AntiWeb extends Module {
    public AntiWeb() { super("AntiWeb", Category.MOVEMENT, "Паутина становится полностью твёрдой — зайти в неё нельзя, как в обычный блок."); }

    public static boolean solid() {
        AntiWeb m = MyCheat.MODULES.get(AntiWeb.class);
        return m != null && m.isEnabled();
    }
}
