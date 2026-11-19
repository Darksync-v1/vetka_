package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

/** Sends a short chat message when the last player attacked by the user dies. */
public final class AutoEZ extends Module {
    private int lastTargetId = -1;
    private long lastAttackAt;
    private long lastMessageAt;

    public AutoEZ() {
        super("AutoEZ", Category.MISC, "Отправляет сообщение после убийства противника.");
    }

    public void onAttack(Entity target) {
        if (!isEnabled() || !(target instanceof PlayerEntity) || target == mc().player) return;
        lastTargetId = target.getId();
        lastAttackAt = System.currentTimeMillis();
    }

    public void onEntityDeath(Entity entity) {
        if (!isEnabled() || mc().player == null || !(entity instanceof PlayerEntity)) return;
        long now = System.currentTimeMillis();
        if (entity.getId() != lastTargetId || now - lastAttackAt > 12_000L || now - lastMessageAt < 1_500L) return;

        String name = ((PlayerEntity) entity).getGameProfile().getName();
        if (mc().getNetworkHandler() == null) return;
        mc().getNetworkHandler().sendChatMessage("я победил тебя ты всегда будешь слабее меня " + name);
        lastMessageAt = now;
        lastTargetId = -1;
    }

    @Override protected void onDisable() {
        lastTargetId = -1;
    }
}
