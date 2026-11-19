package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.entity.player.PlayerEntity;

/** Enables vanilla player labels at a configurable scale-independent distance. */
public final class NameTags extends Module {
    private final NumberSetting showSelf = add(new NumberSetting("ShowSelf", 0, 0, 1, 1));
    public NameTags() { super("NameTags", Category.RENDER, "Показывает имена игроков над их головами."); }

    @Override public void onTick() {
        if (mc().world == null) return;
        for (PlayerEntity player : mc().world.getPlayers()) {
            if (player == mc().player && showSelf.getInt() == 0) continue;
            player.setCustomNameVisible(true);
        }
    }

    @Override protected void onDisable() {
        if (mc().world == null) return;
        for (PlayerEntity player : mc().world.getPlayers()) {
            if (player != mc().player) player.setCustomNameVisible(false);
        }
    }
}
