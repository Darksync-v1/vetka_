package dev.mycheat.mixin;

import dev.mycheat.MyCheat;
import dev.mycheat.module.impl.Velocity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import dev.mycheat.module.impl.FakeLag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onEntityVelocityUpdate", at = @At("HEAD"), cancellable = true)
    private void mycheat$velocity(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || packet.getEntityId() != mc.player.getId()) return;
        Velocity mod = MyCheat.MODULES.get(Velocity.class);
        if (mod == null || !mod.isEnabled()) return;

        ci.cancel();
        double h = mod.horizontal.get() / 100.0, v = mod.vertical.get() / 100.0;
        if (h == 0 && v == 0) return;
        double x = packet.getVelocityX() * h, y = packet.getVelocityY() * v, z = packet.getVelocityZ() * h;
        mc.execute(() -> { if (mc.player != null) mc.player.setVelocity(x, y, z); });
    }

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    private void mycheat$fakeLag(Packet<?> packet, CallbackInfo ci) {
        FakeLag fl = MyCheat.MODULES.get(FakeLag.class);
        if (fl != null && fl.isEnabled() && fl.intercept(packet)) ci.cancel();
    }
}
