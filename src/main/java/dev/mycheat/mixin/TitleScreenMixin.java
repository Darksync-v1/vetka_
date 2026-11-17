package dev.mycheat.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Красит главное меню в тёмно-красный и пишет "VETKA CLIENT" сверху.
 * Рисуем поверх готового экрана (TAIL), поэтому кнопки и фон Minecraft остаются рабочими.
 */
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Text title) { super(title); }

    @Inject(method = "render", at = @At("TAIL"))
    private void vetka$theme(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        context.fill(0, 0, this.width, this.height, 0x661A0000);
        var tr = MinecraftClient.getInstance().textRenderer;
        context.drawCenteredTextWithShadow(tr, Text.literal("VETKA CLIENT").formatted(Formatting.BOLD),
                this.width / 2, 18, 0xFFFFFF);
    }
}
