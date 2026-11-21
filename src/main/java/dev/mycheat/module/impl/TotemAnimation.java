package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Enables/disables Vetka's bundled totem resource pack at runtime. */
public class TotemAnimation extends Module {
    private static final String PACK_ID = "file/Vetka-Totem-Animation";
    private static final String RESOURCE_ROOT = "vetka_resourcepacks/Vetka-Totem-Animation/";
    private static final String[] FILES = {
        "pack.mcmeta", "pack.png",
        "assets/minecraft/models/item/totem_of_undying.json",
        "assets/minecraft/textures/item/totem_of_undying.png",
        "assets/minecraft/sounds.json",
        "assets/minecraft/sounds/item/totem/use_totem.ogg"
    };

    public TotemAnimation() {
        super("TotemAnimation", Category.RENDER,
              "Custom totem texture, in-hand model and activation sound from the Haimiya pack.");
    }

    @Override protected void onEnable() { setPackEnabled(true); }
    @Override protected void onDisable() { setPackEnabled(false); }

    @Override public void toggle() {
        boolean wasEnabled = isEnabled();
        super.toggle();
        if (mc().player == null && wasEnabled != isEnabled()) setPackEnabled(isEnabled());
    }

    private void setPackEnabled(boolean enabled) {
        try {
            Path packDir = FabricLoader.getInstance().getGameDir()
                    .resolve("resourcepacks").resolve("Vetka-Totem-Animation");
            if (enabled) installPack(packDir);

            MinecraftClient client = MinecraftClient.getInstance();
            Object manager = client.getClass().getMethod("getResourcePackManager").invoke(client);
            manager.getClass().getMethod("scanPacks").invoke(manager);
            Method action = manager.getClass().getMethod(enabled ? "enable" : "disable", String.class);
            action.invoke(manager, PACK_ID);
            client.getClass().getMethod("reloadResources").invoke(client);
        } catch (Exception e) {
            System.err.println("[Vetka] Could not " + (enabled ? "enable" : "disable") + " TotemAnimation resource pack: " + e);
        }
    }

    private static void installPack(Path target) throws Exception {
        Files.createDirectories(target);
        ClassLoader loader = TotemAnimation.class.getClassLoader();
        for (String file : FILES) {
            Path out = target.resolve(file);
            Files.createDirectories(out.getParent());
            try (InputStream in = loader.getResourceAsStream(RESOURCE_ROOT + file)) {
                if (in == null) throw new IllegalStateException("Missing bundled resource: " + file);
                Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
