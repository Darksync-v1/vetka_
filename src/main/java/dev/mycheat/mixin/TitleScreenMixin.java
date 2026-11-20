package dev.mycheat.mixin;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import dev.mycheat.gui.AccountManagerScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Custom image background and Vetka title while keeping the vanilla title-screen buttons. */
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Text title) { super(title); }


    @Inject(method = "init", at = @At("TAIL"), require = 0)
    private void vetka$accountManagerButton(CallbackInfo ci) {
        // Remove the Realms entry without changing the other vanilla buttons.
        this.children().removeIf(element ->
                element instanceof ButtonWidget button
                        && button.getMessage().getString().toLowerCase(java.util.Locale.ROOT).contains("realms"));

        TitleScreen self = (TitleScreen) (Object) this;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Account Manager"),
                button -> net.minecraft.client.MinecraftClient.getInstance()
                        .setScreen(new AccountManagerScreen(self)))
                .dimensions(this.width / 2 - 100, this.height - 48, 200, 20).build());
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true, require = 0)
    private void vetka$theme(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ci.cancel();
        // Replace the old black title-screen background with the client background image.
        context.drawTexture(Identifier.of("mycheat", "textures/gui/vetka_bg.png"),
                0, 0, 0, 0, this.width, this.height, 3840, 2160);
        context.fill(0, 0, this.width, this.height, 0x55000000);

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
