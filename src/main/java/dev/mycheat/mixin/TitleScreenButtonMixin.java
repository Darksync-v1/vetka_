package dev.mycheat.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Makes only the three primary title-screen buttons transparent while preserving
 * their original text and font rendering.
 */
@Mixin(ButtonWidget.class)
public abstract class TitleScreenButtonMixin {
    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true, require = 0)
    private void vetka$transparentMainMenuButtons(DrawContext context, int mouseX, int mouseY,
                                                    float delta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!(client.currentScreen instanceof TitleScreen)) return;

        ButtonWidget button = (ButtonWidget) (Object) this;
        String label = button.getMessage().getString().toLowerCase(java.util.Locale.ROOT);
        boolean target = label.contains("одиночная игра")
                || label.contains("сетевая игра")
                || label.contains("майнкрафт реалмс")
                || label.contains("minecraft realms")
                || label.contains("singleplayer")
                || label.contains("multiplayer");
        if (!target) return;

        ci.cancel();
        int color = button.active ? 0xFFFFFFFF : 0xFFA0A0A0;
        context.drawCenteredTextWithShadow(client.textRenderer, button.getMessage(),
                button.getX() + button.getWidth() / 2,
                button.getY() + (button.getHeight() - 8) / 2, color);
    }
}
