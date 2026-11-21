package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

/** Records recent remote-player positions for latency/history inspection. */
public class BackTrack extends Module {
    public final NumberSetting historyTicks = add(new NumberSetting("HistoryTicks", 20, 1, 100, 1));
    public final NumberSetting sampleInterval = add(new NumberSetting("SampleInterval", 1, 1, 10, 1));
    private static final Deque<Sample> HISTORY = new ArrayDeque<>();
    private int tick;
    public record Sample(UUID player, Vec3d position, long time) {}
    public BackTrack() { super("BackTrack", Category.COMBAT, "Записывает историю позиций игроков для анализа движения; не задерживает атаки и не меняет пакеты."); }
    @Override protected void onEnable() { HISTORY.clear(); tick = 0; }
    @Override protected void onDisable() { HISTORY.clear(); }
    @Override public void onTick() {
        if (mc().world == null) return;
        if (++tick % sampleInterval.getInt() != 0) return;
        for (Entity e : mc().world.getEntities()) if (e instanceof PlayerEntity p && p != mc().player && p.isAlive()) {
            HISTORY.addLast(new Sample(p.getUuid(), p.getPos(), System.currentTimeMillis()));
        }
        int max = historyTicks.getInt() * 40;
        while (HISTORY.size() > max) HISTORY.removeFirst();
    }
    public static Deque<Sample> history() { return new ArrayDeque<>(HISTORY); }
}
