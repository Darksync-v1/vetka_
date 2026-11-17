package dev.mycheat.util;

import java.util.ArrayDeque;
import java.util.Deque;

/** Всплывающие уведомления о включении/выключении модулей (как тосты). */
public final class Notifications {
    public record Entry(String name, boolean enabled, long time) {}
    private static final Deque<Entry> QUEUE = new ArrayDeque<>();

    public static void add(String name, boolean enabled) {
        QUEUE.addFirst(new Entry(name, enabled, System.currentTimeMillis()));
        while (QUEUE.size() > 5) QUEUE.removeLast();
    }

    public static Iterable<Entry> active() {
        long now = System.currentTimeMillis();
        QUEUE.removeIf(e -> now - e.time() > 2500);
        return QUEUE;
    }
}
