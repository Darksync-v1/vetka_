package dev.mycheat.mixin;

import dev.mycheat.MyCheat;
import dev.mycheat.module.impl.Velocity;
import dev.mycheat.module.impl.MessageAppend;
import dev.mycheat.module.impl.MessagePrefix;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @ModifyVariable(method = "sendChatMessage", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private String mycheat$messageFormat(String message) {
        if (message == null || message.isBlank() || message.startsWith("/")) return message;
        MessagePrefix prefix = MyCheat.MODULES.get(MessagePrefix.class);
        MessageAppend append = MyCheat.MODULES.get(MessageAppend.class);
        String result = message;
        if (prefix != null && prefix.isEnabled() && !prefix.message.get().isEmpty())
            result = prefix.message.get() + result;
        if (append != null && append.isEnabled() && !append.message.get().isEmpty())
            result = result + append.message.get();
        return result;
    }

    @Inject(method = "onEntityVelocityUpdate", at = @At("HEAD"), cancellable = true, require = 0)
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
}
