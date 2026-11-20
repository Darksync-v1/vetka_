package dev.mycheat;

import dev.mycheat.gui.ClickGui;
import dev.mycheat.gui.VetkaHudNewGui;
import dev.mycheat.module.impl.VetkaHudNew;
import dev.mycheat.module.Module;
import dev.mycheat.module.ModuleManager;
import dev.mycheat.module.impl.ClickTP;
import dev.mycheat.module.impl.ESP;
import dev.mycheat.util.Notifications;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import dev.mycheat.module.impl.TargetESP;
import dev.mycheat.module.impl.ChinaHat;
import dev.mycheat.module.impl.PhotoMath;
import dev.mycheat.util.Config;
import dev.mycheat.util.Friends;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import org.lwjgl.glfw.GLFW;

public class MyCheat implements ClientModInitializer {
    public static final ModuleManager MODULES = new ModuleManager();
    private static KeyBinding guiKey;
    private static boolean middleDown;

    @Override
    public void onInitializeClient() {
        Config.load();
        guiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mycheat.gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.mycheat"));
        WorldRenderEvents.LAST.register(ctx -> {
            TargetESP tesp = MODULES.get(TargetESP.class);
            if (tesp != null && tesp.isEnabled()) tesp.render(ctx);
            ESP esp = MODULES.get(ESP.class);
            if (esp != null && esp.isEnabled()) esp.render(ctx);
            ChinaHat hat = MODULES.get(ChinaHat.class);
            if (hat != null && hat.isEnabled()) hat.render(ctx);
        });
        HudRenderCallback.EVENT.register((ctx, tickCounter) -> drawHud(ctx));
        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
            PhotoMath photoMath = MODULES.get(PhotoMath.class);
            if (photoMath != null && photoMath.isEnabled()) photoMath.onIncomingMessage(message);
        });
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            PhotoMath photoMath = MODULES.get(PhotoMath.class);
            if (photoMath != null && photoMath.isEnabled()) photoMath.onIncomingMessage(message);
        });
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (guiKey.wasPressed()) { VetkaHudNew mode = MODULES.get(VetkaHudNew.class); mc.setScreen(mode != null && mode.isEnabled() ? new VetkaHudNewGui() : new ClickGui()); }
            if (mc.player == null || mc.world == null) return;
            if (mc.currentScreen == null) handleInput(mc);
            MODULES.tick(mc);
        });
    }

    private static void handleInput(MinecraftClient mc) {
        long w = mc.getWindow().getHandle();

        // бинды модулей
        for (Module m : MODULES.getAll()) {
            int k = m.getKey();
            if (k < 0) continue;
            boolean down = InputUtil.isKeyPressed(w, k);
            if (down && !m.keyDown) m.toggle();
            m.keyDown = down;
        }

        // СКМ: по игроку = друг, иначе ClickTP
        boolean md = GLFW.glfwGetMouseButton(w, GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS;
        if (md && !middleDown) {
            if (mc.crosshairTarget instanceof EntityHitResult hit && hit.getEntity() instanceof PlayerEntity pl) {
                String name = pl.getGameProfile().getName();
                boolean added = Friends.toggle(name);
                mc.player.sendMessage(Text.literal(added ? "§a[Vetka] §fДруг добавлен: " + name
                        : "§c[Vetka] §fДруг удалён: " + name), false);
                Config.save();
            } else {
                ClickTP tp = MODULES.get(ClickTP.class);
                if (tp != null && tp.isEnabled()) tp.teleport();
            }
        }
        middleDown = md;
    }

    private static void drawHud(DrawContext ctx) {
        var crosshair = MODULES.get(dev.mycheat.module.impl.Crosshair.class);
        if (crosshair != null) crosshair.render(ctx);
        var coordinates = MODULES.get(dev.mycheat.module.impl.Coordinates.class);
        if (coordinates != null) coordinates.render(ctx);
        var fps = MODULES.get(dev.mycheat.module.impl.FPS.class);
        if (fps != null) fps.render(ctx);
        var armor = MODULES.get(dev.mycheat.module.impl.ArmorStatus.class);
        if (armor != null) armor.render(ctx);
        var targetHud = MODULES.get(dev.mycheat.module.impl.TargetHUD.class);
        if (targetHud != null && targetHud.isEnabled()) targetHud.render(ctx);
        MinecraftClient client = MinecraftClient.getInstance();
        var tr = client.textRenderer;
        int y = 4;
        for (var m : MODULES.getAll()) {
            if (!m.isEnabled()) continue;
            int w = tr.getWidth(m.getName());
            ctx.drawTextWithShadow(tr, "§d" + m.getName(), client.getWindow().getScaledWidth() - w - 6, y, 0xFFFFFF);
            y += 10;
        }
        long now = System.currentTimeMillis();
        int ny = client.getWindow().getScaledHeight() - 24;
        for (Notifications.Entry e : Notifications.active()) {
            float age = (now - e.time()) / 2500f;
            int alpha = (int) (255 * (age < 0.8f ? 1f : (1f - age) / 0.2f));
            String text = (e.enabled() ? "§a» §f" : "§c» §f") + e.name();
            int w = tr.getWidth(text);
            int bx = client.getWindow().getScaledWidth() - w - 14;
            ctx.fill(bx - 4, ny - 2, bx + w + 6, ny + 9, (alpha << 24 & 0xFF000000) | 0x14141E);
            ctx.drawTextWithShadow(tr, text, bx, ny, 0xFFFFFF | (alpha << 24));
            ny -= 12;
        }
    }
}
