package dev.mycheat.mixin;

import dev.mycheat.MyCheat;
import dev.mycheat.module.impl.FakeLag;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** sendPacket объявлен в ClientCommonNetworkHandler (общий для play/configuration), а не в самом ClientPlayNetworkHandler. */
@Mixin(ClientCommonNetworkHandler.class)
public class ClientCommonNetworkHandlerMixin {
    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true, require = 0)
    private void mycheat$fakeLag(Packet<?> packet, CallbackInfo ci) {
        FakeLag fl = MyCheat.MODULES.get(FakeLag.class);
        if (fl != null && fl.isEnabled() && fl.intercept(packet)) ci.cancel();
    }
}
