package dev.mycheat.mixin;

import dev.mycheat.module.impl.AntiWeb;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Makes cobweb collision a full cube while AntiWeb is enabled. */
@Mixin(AbstractBlock.class)
public abstract class CobwebMixin {
    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void vetka$antiWeb(BlockState state, BlockView world, BlockPos pos, ShapeContext context,
                               CallbackInfoReturnable<VoxelShape> cir) {
        if (state.isOf(Blocks.COBWEB) && AntiWeb.solid()) {
            cir.setReturnValue(VoxelShapes.fullCube());
        }
    }
}
