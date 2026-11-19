package dev.mycheat.mixin;

import dev.mycheat.MyCheat;
import dev.mycheat.module.impl.NoServerRotate;
import dev.mycheat.module.impl.NoServerSlot;
import dev.mycheat.module.impl.AutoEZ;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.LookAtS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Экспериментально: отменяет пакеты, которыми сервер принудительно поворачивает камеру
 * или меняет выбранный слот хотбара. require = 0 — если сервер/версия называет методы иначе,
 * эти две функции просто не сработают, но игра не вылетит.
 */
@Mixin(ClientPlayNetworkHandler.class)
public class IncomingPacketsMixin {
    @Inject(method = "onLookAt", at = @At("HEAD"), cancellable = true, require = 0)
    private void mycheat$noRotate(LookAtS2CPacket packet, CallbackInfo ci) {
        NoServerRotate m = MyCheat.MODULES.get(NoServerRotate.class);
        if (m != null && m.isEnabled()) ci.cancel();
    }

    @Inject(method = "onUpdateSelectedSlot", at = @At("HEAD"), cancellable = true, require = 0)
    private void mycheat$noSlot(UpdateSelectedSlotS2CPacket packet, CallbackInfo ci) {
        NoServerSlot m = MyCheat.MODULES.get(NoServerSlot.class);
        if (m != null && m.isEnabled()) ci.cancel();
    }

    @Inject(method = "onEntityStatus", at = @At("TAIL"), require = 0)
    private void mycheat$autoEz(EntityStatusS2CPacket packet, CallbackInfo ci) {
        if (packet.getStatus() != 3) return;
        if (net.minecraft.client.MinecraftClient.getInstance().world == null) return;
        var entity = packet.getEntity(net.minecraft.client.MinecraftClient.getInstance().world);
        AutoEZ autoEZ = MyCheat.MODULES.get(AutoEZ.class);
        if (autoEZ != null) autoEZ.onEntityDeath(entity);
    }
}
