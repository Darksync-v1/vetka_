package dev.mycheat.mixin;

import dev.mycheat.module.impl.AntiWeb;
import net.minecraft.block.BlockState;
import net.minecraft.block.CobwebBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Экспериментально (require = 0): делает паутину полностью твёрдой блоком для AntiWeb. */
@Mixin(CobwebBlock.class)
public class CobwebMixin {
    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true, require = 0)
    private void vetka$antiWeb(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx,
                                CallbackInfoReturnable<VoxelShape> cir) {
        if (AntiWeb.solid()) cir.setReturnValue(VoxelShapes.fullCube());
    }
}
