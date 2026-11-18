package dev.mycheat.mixin;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Полностью чёрный главный экран без логотипа Minecraft: своя картинка и надпись "Vetka" белым жирным
 * шрифтом вместо логотипа. Оставляет видимыми и рабочими только кнопки "Singleplayer" (Одиночная игра),
 * "Multiplayer" (Сетевая игра) и "Minecraft Realms". require = 0 — если название метода у Mojang другое,
 * экран остаётся обычным, игра не падает.
 */
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    private static final String[] KEEP = {
            "Singleplayer", "Multiplayer", "Minecraft Realms", "Одиночная игра", "Сетевая игра"
    };
    private static final Identifier BG = Identifier.of("mycheat", "textures/gui/vetka_bg.png");
    private static final int BG_W = 220, BG_H = 391;

    protected TitleScreenMixin(Text title) { super(title); }

    private static boolean keep(ClickableWidget w) {
        String text = w.getMessage().getString();
        for (String k : KEEP) if (text.equalsIgnoreCase(k)) return true;
        return false;
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true, require = 0)
    private void vetka$theme(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ci.cancel();
        context.fill(0, 0, this.width, this.height, 0xFF000000);

        int ix = (this.width - BG_W) / 2;
        int iy = 40;
        context.drawTexture(BG, ix, iy, 0, 0, BG_W, BG_H, BG_W, BG_H);

        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Vetka").formatted(Formatting.BOLD), this.width / 2, 18, 0xFFFFFF);

        for (Element e : this.children()) {
            if (e instanceof ClickableWidget w && keep(w) && e instanceof Drawable d) {
                d.render(context, mouseX, mouseY, delta);
            }
        }
    }
}
