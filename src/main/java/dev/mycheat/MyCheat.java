package dev.mycheat;

import dev.mycheat.gui.ClickGui;
import dev.mycheat.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class MyCheat implements ClientModInitializer {
    public static final ModuleManager MODULES = new ModuleManager();
    private static KeyBinding guiKey;

    @Override
    public void onInitializeClient() {
        guiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mycheat.gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.mycheat"));
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (guiKey.wasPressed()) mc.setScreen(new ClickGui());
            if (mc.player != null && mc.world != null) MODULES.tick(mc);
        });
    }
}
