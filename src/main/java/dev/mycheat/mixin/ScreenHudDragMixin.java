package dev.mycheat.mixin;

import dev.mycheat.util.HudDragHandler;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class ScreenHudDragMixin {
    @Inject(method="mouseClicked", at=@At("HEAD"), cancellable=true, require=0)
    private void vetka$beginHudDrag(double mouseX,double mouseY,int button,CallbackInfoReturnable<Boolean> cir) {
        if (button==1 && HudDragHandler.begin(mouseX,mouseY)) cir.setReturnValue(true);
    }

    @Inject(method="mouseDragged", at=@At("HEAD"), cancellable=true, require=0)
    private void vetka$dragHud(double mouseX,double mouseY,int button,double deltaX,double deltaY,CallbackInfoReturnable<Boolean> cir) {
        if (HudDragHandler.drag(mouseX,mouseY,button)) cir.setReturnValue(true);
    }

    @Inject(method="mouseReleased", at=@At("HEAD"), cancellable=true, require=0)
    private void vetka$releaseHud(double mouseX,double mouseY,int button,CallbackInfoReturnable<Boolean> cir) {
        if (HudDragHandler.release(button)) cir.setReturnValue(true);
    }
}
