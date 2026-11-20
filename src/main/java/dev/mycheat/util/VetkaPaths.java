package dev.mycheat.util;

import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.Path;

/** Central storage paths for Vetka Client inside the Minecraft game directory. */
public final class VetkaPaths {
    private VetkaPaths() {}

    /** Example: .minecraft/Vetka */
    public static Path root() {
        return FabricLoader.getInstance().getGameDir().resolve("Vetka");
    }

    /** Example: .minecraft/Vetka/config */
    public static Path config() {
        return root().resolve("config");
    }
}
