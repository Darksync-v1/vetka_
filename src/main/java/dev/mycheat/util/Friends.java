package dev.mycheat.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class Friends {
    private static final Set<String> NAMES = new LinkedHashSet<>();

    public static boolean isFriend(Entity e) {
        return e instanceof PlayerEntity p && NAMES.contains(p.getGameProfile().getName().toLowerCase());
    }
    public static void add(String name) { NAMES.add(name.toLowerCase()); }
    public static void remove(String name) { NAMES.remove(name.toLowerCase()); }
    /** @return true, если друг добавлен; false, если удалён */
    public static boolean toggle(String name) {
        String n = name.toLowerCase();
        if (NAMES.remove(n)) return false;
        NAMES.add(n);
        return true;
    }
    public static List<String> all() { return new ArrayList<>(NAMES); }
}
