package dev.mycheat.mixin;

import dev.mycheat.MyCheat;
import dev.mycheat.module.impl.NameTags;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Makes vanilla's normal player label renderer consider player entities label-visible. */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
    @Inject(method = "hasLabel", at = @At("RETURN"), cancellable = true, require = 0)
    private void vetka$showPlayerNames(T entity,
                                       CallbackInfoReturnable<Boolean> cir) {
        NameTags tags = MyCheat.MODULES.get(NameTags.class);
        if (tags == null || !tags.isEnabled() || !(entity instanceof PlayerEntity)) return;
        if (entity == net.minecraft.client.MinecraftClient.getInstance().player && !tags.shouldShowSelf()) return;
        cir.setReturnValue(true);
    }
}
