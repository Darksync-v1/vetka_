package dev.mycheat.mixin;

import dev.mycheat.MyCheat;
import dev.mycheat.module.impl.Phase;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void mycheat$phase(CallbackInfo ci) {
        Phase ph = MyCheat.MODULES.get(Phase.class);
        if (ph != null && ph.isEnabled()) ((ClientPlayerEntity) (Object) this).noClip = ph.shouldClip();
    }
}
