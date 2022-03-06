package azonix.endlessvoid.mixin;

import azonix.endlessvoid.EndlessVoidCommon;
import azonix.endlessvoid.common.block.EnderlockBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Player.class)
public abstract class PlayerEntityMixin {
    @Inject(method = "findRespawnPositionAndUseSpawnBlock", at = @At(value = "HEAD"), cancellable = true)
    private static void findRespawn(ServerLevel world, BlockPos pos, float f, boolean bl, boolean bl2, CallbackInfoReturnable<Optional<Vec3>> cir) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();

        if (EndlessVoidCommon.respawnAfterCredits) {
            EndlessVoidCommon.respawnAfterCredits = false;
            return;
        }

        if (block instanceof EnderlockBlock && blockState.getValue(EnderlockBlock.CHARGES) > 0 && EnderlockBlock.isEnd(world)) {
            Optional<Vec3> optional = EnderlockBlock.findRespawnPosition(EntityType.PLAYER, world, pos);
            if (!bl2 && optional.isPresent()) {
                world.setBlock(pos, blockState.setValue(EnderlockBlock.CHARGES, blockState.getValue(EnderlockBlock.CHARGES) - 1), Block.UPDATE_ALL);
            }

            cir.setReturnValue(optional);
        }
    }
}
