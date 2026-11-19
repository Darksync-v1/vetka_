package dev.mycheat.module.impl;

import dev.mycheat.MyCheat;
import dev.mycheat.module.Category;
import dev.mycheat.module.Module;

/** While enabled, cobwebs have a full-block collision shape and cannot be entered. */
public class AntiWeb extends Module {
    public AntiWeb() {
        super("AntiWeb", Category.MOVEMENT,
                "Делает паутину твёрдым блоком: при включении в неё нельзя войти.");
    }

    public static boolean solid() {
        AntiWeb module = MyCheat.MODULES.get(AntiWeb.class);
        return module != null && module.isEnabled();
    }
}
