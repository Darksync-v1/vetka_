package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;

/** Enables the vanilla entity-name label renderer for player entities on this client. */
public final class NameTags extends Module {
    private final NumberSetting showSelf = add(new NumberSetting("ShowSelf", 0, 0, 1, 1));

    public NameTags() {
        super("NameTags", Category.RENDER,
                "Принудительно показывает ванильные имена игроков над головами на клиенте.");
    }

    public boolean shouldShowSelf() {
        return showSelf.getInt() == 1;
    }
}
