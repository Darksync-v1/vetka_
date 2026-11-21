package dev.mycheat.mixin;

import dev.mycheat.module.impl.Jesus;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidBlock.class)
public class FluidBlockMixin {
    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void mycheat$solidWater(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx,
                                    CallbackInfoReturnable<VoxelShape> cir) {
        if (!Jesus.solid()) return;
        var p = MinecraftClient.getInstance().player;
        if (p == null || p.isSneaking() || !state.getFluidState().isIn(FluidTags.WATER)) return;
        // Делаем воду полноценной опорной поверхностью только для игрока,
        // который находится над ней. Подводное движение и выход снизу остаются возможны.
        if (p.getY() < pos.getY() + 0.5) return;
        cir.setReturnValue(VoxelShapes.fullCube());
    }
}
