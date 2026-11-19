package dev.mycheat.mixin;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Единый чёрный фон на весь экран (без отдельного "пятна" под текстом), без логотипа Minecraft
 * и без жёлтого сплэш-текста — просто ничего из ванильного рендера не рисуем (cancel).
 * "Vetka Client" рисуется прямо на чёрном фоне без отдельной плашки. Все обычные кнопки
 * (Singleplayer, Multiplayer, Realms, Options и т.д.) остаются и работают как раньше.
 */
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Text title) { super(title); }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true, require = 0)
    private void vetka$theme(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ci.cancel();
        context.fill(0, 0, this.width, this.height, 0xFF000000);

        float scale = 4f;
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1f);
        int cx = (int) ((this.width / 2f) / scale);
        int cy = (int) (24 / scale);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Vetka Client").formatted(Formatting.BOLD), cx, cy, 0xFFFFFF);
        context.getMatrices().pop();

        for (Element e : this.children()) {
            if (e instanceof Drawable d) d.render(context, mouseX, mouseY, delta);
        }
    }
}
