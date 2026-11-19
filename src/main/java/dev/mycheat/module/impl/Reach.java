package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import dev.mycheat.util.Friends;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

/**
 * Увеличивает дистанцию ручной атаки по ЛКМ (не автоматически, как аура — бьёт только пока сам
 * зажимаешь ЛКМ). Обычный клик у игры ограничен ~3 блоками; эта функция ищет ближайшую к лучу
 * взгляда цель в радиусе Range и бьёт напрямую, в обход этого ограничения клиента.
 * Сервер может не принять удар дальше своего собственного лимита reach.
 */
public class Reach extends Module {
    private final NumberSetting range = add(new NumberSetting("Range", 5.0, 3.0, 10.0, 0.5));
    private boolean wasDown;

    public Reach() { super("Reach", Category.COMBAT, "Увеличивает дистанцию ручной атаки по ЛКМ. Бьёт, только пока сам зажимаешь левую кнопку мыши."); }

    @Override public void onTick() {
        var p = mc().player;
        long win = mc().getWindow().getHandle();
        boolean down = mc().currentScreen == null && GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean justPressed = down && !wasDown;
        wasDown = down;
        if (!justPressed || p.getAttackCooldownProgress(0.5f) < 0.95f) return;

        Vec3d eyes = p.getEyePos();
        Vec3d look = p.getRotationVec(1.0f);
        Entity best = null;
        double bestScore = Double.MAX_VALUE;
        for (Entity e : mc().world.getEntities()) {
            if (e == p || !(e instanceof PlayerEntity pl) || !pl.isAlive() || pl.isSpectator() || pl.isCreative() || Friends.isFriend(pl)) continue;
            Vec3d toEnt = e.getBoundingBox().getCenter().subtract(eyes);
            double dist = toEnt.length();
            if (dist > range.get()) continue;
            double along = toEnt.normalize().dotProduct(look); // насколько прямо по центру взгляда
            if (along < 0.90) continue; // должен быть почти точно под прицелом
            double score = dist * (1.02 - along);
            if (score < bestScore) { bestScore = score; best = e; }
        }
        if (best == null) return;
        mc().interactionManager.attackEntity(p, best);
        p.swingHand(Hand.MAIN_HAND);
    }
}
