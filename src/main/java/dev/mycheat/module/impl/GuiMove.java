package dev.mycheat.module.impl;

import dev.mycheat.MyCheat;
import dev.mycheat.module.Category;
import dev.mycheat.module.Module;

/** Переключатель режима перетаскивания меню: включи, зайди в меню (Right Shift) и таскай колонки за заголовок. */
public class GuiMove extends Module {
    public GuiMove() {
        super("GuiMove", Category.MISC, "Режим перетаскивания меню. Включи, открой меню (Right Shift) и таскай колонки мышью за заголовок категории.");
    }

    public static boolean enabled() {
        GuiMove m = MyCheat.MODULES.get(GuiMove.class);
        return m != null && m.isEnabled();
    }
}
