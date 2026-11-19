package dev.mycheat.mixin;

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
 * Обычный главный экран Minecraft, только в области логотипа — закрашенный чёрным прямоугольник
 * и поверх него надпись "Vetka Client" крупным белым жирным шрифтом. Всё остальное (фон, кнопки,
 * версия, копирайт) остаётся стандартным, так как мы ничего не отменяем (не cancel), а просто
 * дорисовываем поверх в конце (TAIL). require = 0 — на случай другого имени метода у Mojang.
 */
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Text title) { super(title); }

    @Inject(method = "render", at = @At("TAIL"), require = 0)
    private void vetka$theme(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int top = 20, bottom = 90;
        context.fill(0, top, this.width, bottom, 0xFF000000);

        float scale = 4f;
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1f);
        int cx = (int) ((this.width / 2f) / scale);
        int cy = (int) ((top + (bottom - top) / 2f - 4) / scale);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Vetka Client").formatted(Formatting.BOLD), cx, cy, 0xFFFFFF);
        context.getMatrices().pop();
    }
}
