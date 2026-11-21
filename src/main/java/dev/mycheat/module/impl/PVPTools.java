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

/** Enables/disables Vetka's bundled PVP tools item-texture pack. */
public class PVPTools extends Module {
    private static final String PACK_ID = "file/Vetka-PVP-Tools";
    private static final String RESOURCE_ROOT = "vetka_resourcepacks/Vetka-PVP-Tools/";

    private static final String[] FILES = {
        "pack.mcmeta", "pack.png",
        "assets/minecraft/textures/item/bow.png",
        "assets/minecraft/textures/item/bow_pulling_0.png",
        "assets/minecraft/textures/item/bow_pulling_1.png",
        "assets/minecraft/textures/item/bow_pulling_2.png",
        "assets/minecraft/textures/item/crossbow_arrow.png",
        "assets/minecraft/textures/item/crossbow_firework.png",
        "assets/minecraft/textures/item/crossbow_pulling_0.png",
        "assets/minecraft/textures/item/crossbow_pulling_1.png",
        "assets/minecraft/textures/item/crossbow_pulling_2.png",
        "assets/minecraft/textures/item/crossbow_standby.png",
        "assets/minecraft/textures/item/diamond_axe.png",
        "assets/minecraft/textures/item/diamond_hoe.png",
        "assets/minecraft/textures/item/diamond_pickaxe.png",
        "assets/minecraft/textures/item/diamond_shovel.png",
        "assets/minecraft/textures/item/diamond_sword.png",
        "assets/minecraft/textures/item/flint_and_steel.png",
        "assets/minecraft/textures/item/golden_axe.png",
        "assets/minecraft/textures/item/golden_hoe.png",
        "assets/minecraft/textures/item/golden_pickaxe.png",
        "assets/minecraft/textures/item/golden_shovel.png",
        "assets/minecraft/textures/item/golden_sword.png",
        "assets/minecraft/textures/item/iron_axe.png",
        "assets/minecraft/textures/item/iron_hoe.png",
        "assets/minecraft/textures/item/iron_pickaxe.png",
        "assets/minecraft/textures/item/iron_shovel.png",
        "assets/minecraft/textures/item/iron_sword.png",
        "assets/minecraft/textures/item/netherite_axe.png",
        "assets/minecraft/textures/item/netherite_hoe.png",
        "assets/minecraft/textures/item/netherite_pickaxe.png",
        "assets/minecraft/textures/item/netherite_shovel.png",
        "assets/minecraft/textures/item/netherite_sword.png",
        "assets/minecraft/textures/item/stone_axe.png",
        "assets/minecraft/textures/item/stone_hoe.png",
        "assets/minecraft/textures/item/stone_pickaxe.png",
        "assets/minecraft/textures/item/stone_shovel.png",
        "assets/minecraft/textures/item/stone_sword.png",
        "assets/minecraft/textures/item/wooden_axe.png",
        "assets/minecraft/textures/item/wooden_hoe.png",
        "assets/minecraft/textures/item/wooden_pickaxe.png",
        "assets/minecraft/textures/item/wooden_shovel.png",
        "assets/minecraft/textures/item/wooden_sword.png",
        "assets/minecraft/textures/item/trident.png",
        "assets/minecraft/textures/item/warped_fungus_on_a_stick.png",
        "assets/minecraft/textures/item/spyglass.png",
        "assets/minecraft/textures/item/shears.png",
        "assets/minecraft/textures/item/fishing_rod_cast.png",
        "assets/minecraft/textures/item/fishing_rod.png",
        "assets/minecraft/textures/item/carrot_on_a_stick.png",
        "assets/minecraft/textures/item/brush.png",
        "assets/minecraft/textures/item/bucket.png",
        "assets/minecraft/textures/item/cod_bucket.png",
        "assets/minecraft/textures/item/lava_bucket.png",
        "assets/minecraft/textures/item/milk_bucket.png",
        "assets/minecraft/textures/item/powder_snow_bucket.png",
        "assets/minecraft/textures/item/pufferfish_bucket.png",
        "assets/minecraft/textures/item/salmon_bucket.png",
        "assets/minecraft/textures/item/tadpole_bucket.png",
        "assets/minecraft/textures/item/tropical_fish_bucket.png",
        "assets/minecraft/textures/item/water_bucket.png",
        "assets/minecraft/textures/item/axolotl_bucket.png",
        "assets/minecraft/textures/item/mace.png",
        "assets/minecraft/textures/item/goat_horn.png"
    };

    public PVPTools() {
        super("PVPTools", Category.RENDER,
              "Changes tool, weapon, bow and utility-item textures using the bundled PVP pack.");
    }

    @Override protected void onEnable() { setPackEnabled(true); }
    @Override protected void onDisable() { setPackEnabled(false); }

    private void setPackEnabled(boolean enabled) {
        try {
            Path packDir = FabricLoader.getInstance().getGameDir()
                    .resolve("resourcepacks").resolve("Vetka-PVP-Tools");
            if (enabled) installPack(packDir);

            MinecraftClient client = MinecraftClient.getInstance();
            Object manager = client.getClass().getMethod("getResourcePackManager").invoke(client);
            manager.getClass().getMethod("scanPacks").invoke(manager);
            Method action = manager.getClass().getMethod(enabled ? "enable" : "disable", String.class);
            action.invoke(manager, PACK_ID);
            client.getClass().getMethod("reloadResources").invoke(client);
        } catch (Exception e) {
            System.err.println("[Vetka] Could not " + (enabled ? "enable" : "disable") + " PVPTools resource pack: " + e);
        }
    }

    private static void installPack(Path target) throws Exception {
        Files.createDirectories(target);
        ClassLoader loader = PVPTools.class.getClassLoader();
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
