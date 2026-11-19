package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Renders real player-name labels by assigning a client-side custom name. */
public final class NameTags extends Module {
    private final NumberSetting showSelf = add(new NumberSetting("ShowSelf", 0, 0, 1, 1));
    private final Map<UUID, OriginalName> originals = new HashMap<>();

    private record OriginalName(Text name, boolean visible) {}

    public NameTags() {
        super("NameTags", Category.RENDER, "Показывает имена игроков над их головами.");
    }

    @Override
    public void onTick() {
        if (mc().world == null) return;

        for (PlayerEntity player : mc().world.getPlayers()) {
            if (player == mc().player && showSelf.getInt() == 0) {
                restore(player);
                continue;
            }

            originals.putIfAbsent(player.getUuid(),
                    new OriginalName(player.getCustomName(), player.isCustomNameVisible()));

            // PlayerEntity normally has no custom name, so only toggling visibility does nothing.
            // Set the profile name as a client-side label for the vanilla name-tag renderer.
            player.setCustomName(Text.literal(player.getGameProfile().getName()));
            player.setCustomNameVisible(true);
        }
    }

    private void restore(PlayerEntity player) {
        OriginalName original = originals.remove(player.getUuid());
        if (original == null) return;
        player.setCustomName(original.name());
        player.setCustomNameVisible(original.visible());
    }

    @Override
    protected void onDisable() {
        if (mc().world != null) {
            for (PlayerEntity player : mc().world.getPlayers()) restore(player);
        }
        originals.clear();
    }
}
