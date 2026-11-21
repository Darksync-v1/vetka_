package dev.mycheat.util;

import java.util.HashMap;
import java.util.Map;

/** Shared screen positions for movable HUD widgets. */
public final class HudPositions {
    private static final Map<String, int[]> POSITIONS = new HashMap<>();
    private HudPositions() {}

    public static int x(String id, int fallback) { return POSITIONS.getOrDefault(id, new int[]{fallback, 0})[0]; }
    public static int y(String id, int fallback) { return POSITIONS.getOrDefault(id, new int[]{0, fallback})[1]; }
    public static void set(String id, int x, int y) { POSITIONS.put(id, new int[]{Math.max(0,x), Math.max(0,y)}); }
    public static Map<String, int[]> all() { return POSITIONS; }
}
