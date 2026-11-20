package dev.mycheat.util;

import dev.mycheat.MyCheat;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import dev.mycheat.setting.TextSetting;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Сохраняет бинды, настройки и друзей в config/vetka-v1.properties. */
public final class Config {
    private static Path file() { return FabricLoader.getInstance().getConfigDir().resolve("vetka-v1.properties"); }

    public static void load() {
        Properties p = new Properties();
        try (var in = Files.newInputStream(file())) { p.load(in); } catch (Exception e) { return; }
        for (Module m : MyCheat.MODULES.getAll()) {
            try {
                String b = p.getProperty("bind." + m.getName());
                if (b != null) m.setKey(Integer.parseInt(b));
                for (NumberSetting s : m.getSettings()) {
                    String v = p.getProperty("set." + m.getName() + "." + s.name);
                    if (v != null) s.set(Double.parseDouble(v));
                }
                for (TextSetting s : m.getTextSettings()) {
                    String v = p.getProperty("text." + m.getName() + "." + s.name);
                    if (v != null) s.set(v);
                }
            } catch (NumberFormatException ignored) {}
        }
        for (String n : p.getProperty("friends", "").split(",")) if (!n.isBlank()) Friends.add(n.trim());
        var hudNew = MyCheat.MODULES.get(dev.mycheat.module.impl.VetkaHudNew.class);
        if (hudNew != null && Boolean.parseBoolean(p.getProperty("gui.vetkaHudNew", "false")) && !hudNew.isEnabled()) hudNew.toggle();
    }

    public static void save() {
        Properties p = new Properties();
        for (Module m : MyCheat.MODULES.getAll()) {
            p.setProperty("bind." + m.getName(), String.valueOf(m.getKey()));
            for (NumberSetting s : m.getSettings()) p.setProperty("set." + m.getName() + "." + s.name, String.valueOf(s.get()));
            for (TextSetting s : m.getTextSettings()) p.setProperty("text." + m.getName() + "." + s.name, s.get());
        }
        p.setProperty("friends", String.join(",", Friends.all()));
        var hudNew = MyCheat.MODULES.get(dev.mycheat.module.impl.VetkaHudNew.class);
        if (hudNew != null) p.setProperty("gui.vetkaHudNew", String.valueOf(hudNew.isEnabled()));
        try (var out = Files.newOutputStream(file())) { p.store(out, "Vetka v1"); } catch (Exception ignored) {}
    }
}
