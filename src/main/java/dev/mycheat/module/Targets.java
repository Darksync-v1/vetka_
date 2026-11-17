package dev.mycheat.module;

import net.minecraft.entity.Entity;

/** Текущая цель аур, её рисует TargetESP. */
public final class Targets {
    private static Entity target;
    private static long lastSet;

    public static void set(Entity e) { target = e; lastSet = System.currentTimeMillis(); }

    /** Цель, если её выбирали в последние 500 мс и она ещё жива. */
    public static Entity get() {
        if (target == null || !target.isAlive() || System.currentTimeMillis() - lastSet > 500) return null;
        return target;
    }
}
