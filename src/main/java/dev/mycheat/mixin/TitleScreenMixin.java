package dev.mycheat.mixin;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import dev.mycheat.gui.AccountManagerScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Solid-black Vetka title screen with Account Manager replacing the Realms slot. */
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Text title) { super(title); }


    @Inject(method = "init", at = @At("TAIL"), require = 0)
    private void vetka$accountManagerButton(CallbackInfo ci) {
        TitleScreen self = (TitleScreen) (Object) this;
        java.util.List<ButtonWidget> realms = this.children().stream()
                .filter(element -> element instanceof ButtonWidget)
                .map(element -> (ButtonWidget) element)
                .filter(button -> button.getMessage().getString().toLowerCase(java.util.Locale.ROOT).contains("realms"))
                .toList();
        if (!realms.isEmpty()) {
            ButtonWidget old = realms.get(0);
            int x = old.getX(), y = old.getY(), w = old.getWidth(), h = old.getHeight();
            this.remove(old);
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Account Manager"),
                    button -> net.minecraft.client.MinecraftClient.getInstance()
                            .setScreen(new AccountManagerScreen(self)))
                    .dimensions(x, y, w, h).build());
        } else {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Account Manager"),
                    button -> net.minecraft.client.MinecraftClient.getInstance()
                            .setScreen(new AccountManagerScreen(self)))
                    .dimensions(this.width / 2 - 100, this.height - 48, 200, 20).build());
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true, require = 0)
    private void vetka$theme(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ci.cancel();
        // Solid black background as requested.
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
