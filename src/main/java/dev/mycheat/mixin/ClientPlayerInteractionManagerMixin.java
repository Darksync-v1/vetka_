package dev.mycheat.mixin;

import dev.mycheat.MyCheat;
import dev.mycheat.module.impl.Criticals;
import dev.mycheat.module.impl.AutoEZ;
import dev.mycheat.module.impl.TargetStrafe;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void mycheat$criticals(PlayerEntity player, Entity target, CallbackInfo ci) {
        Criticals c = MyCheat.MODULES.get(Criticals.class);
        if (c != null && c.isEnabled()) c.onAttack();
        AutoEZ autoEZ = MyCheat.MODULES.get(AutoEZ.class);
        if (autoEZ != null) autoEZ.onAttack(target);
        TargetStrafe strafe = MyCheat.MODULES.get(TargetStrafe.class);
        if (strafe != null) strafe.onTargetAttack(target);
    }
}
