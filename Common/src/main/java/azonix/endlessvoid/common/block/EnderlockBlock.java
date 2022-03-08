package azonix.endlessvoid.common.block;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import com.google.common.collect.ImmutableList.Builder;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class EnderlockBlock extends Block {

    public static final int NO_CHARGES = 0;
    public static final int MAX_CHARGES = 4;
    public static final IntegerProperty CHARGES = IntegerProperty.create("charges", NO_CHARGES, MAX_CHARGES);
    private static final ImmutableList<Vec3i> VALID_HORIZONTAL_SPAWN_OFFSETS = ImmutableList.of(new Vec3i(0, 0, -1), new Vec3i(-1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(1, 0, 0), new Vec3i(-1, 0, -1), new Vec3i(1, 0, -1), new Vec3i(-1, 0, 1), new Vec3i(1, 0, 1));
    private static final ImmutableList<Vec3i> VALID_SPAWN_OFFSETS = (new Builder()).addAll(VALID_HORIZONTAL_SPAWN_OFFSETS).addAll(VALID_HORIZONTAL_SPAWN_OFFSETS.stream().map(Vec3i::below).iterator()).addAll(VALID_HORIZONTAL_SPAWN_OFFSETS.stream().map(Vec3i::above).iterator()).add((new Vec3i(0, 1, 0))).build();

    public EnderlockBlock(Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(CHARGES, 0));
    }

    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (hand == InteractionHand.MAIN_HAND && !isChargeItem(itemStack) && isChargeItem(player.getItemInHand(InteractionHand.OFF_HAND))) {
            return InteractionResult.PASS;
        } else if (isChargeItem(itemStack) && canCharge(state)) {
            charge(world, pos, state);
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }

            return InteractionResult.sidedSuccess(world.isClientSide);
        } else if (state.getValue(CHARGES) == 0) {
            return InteractionResult.PASS;
        } else if (!isEnd(world)) {
            if (!world.isClientSide) {
                this.explode(state, world, pos);
            }

            return InteractionResult.sidedSuccess(world.isClientSide);
        } else {
            if (!world.isClientSide) {
                ServerPlayer serverPlayerEntity = (ServerPlayer)player;
                if (serverPlayerEntity.getRespawnDimension() != world.dimension() || !pos.equals(serverPlayerEntity.getRespawnPosition())) {
                    serverPlayerEntity.setRespawnPosition(world.dimension(), pos, 0.0F, false, true);
                    world.playSound(null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return InteractionResult.SUCCESS;
                }
            }

            return InteractionResult.CONSUME;
        }
    }

    private static boolean isChargeItem(ItemStack stack) {
        return stack.is(Items.ENDER_PEARL);
    }

    private static boolean canCharge(BlockState state) {
        return state.getValue(CHARGES) < 4;
    }

    public static void charge(Level world, BlockPos pos, BlockState state) {
        world.setBlock(pos, state.setValue(CHARGES, state.getValue(CHARGES) + 1), Block.UPDATE_ALL);
        world.playSound(null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public static boolean isEnd(Level world) {
        return world.dimension().equals(Level.END);
    }

    private void explode(BlockState state, Level world, final BlockPos explodedPos) {
        world.removeBlock(explodedPos, false);
        Stream<Direction> var10000 = Direction.Plane.HORIZONTAL.stream();
        Objects.requireNonNull(explodedPos);
        boolean bl = var10000.map(explodedPos::relative).anyMatch((pos) -> hasStillWater(pos, world));
        final boolean bl2 = bl || world.getFluidState(explodedPos.above()).is(FluidTags.WATER);
        ExplosionDamageCalculator explosionBehavior = new ExplosionDamageCalculator() {
            public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter world, BlockPos pos, BlockState blockState, FluidState fluidState) {
                return pos.equals(explodedPos) && bl2 ? Optional.of(Blocks.WATER.getExplosionResistance()) : super.getBlockExplosionResistance(explosion, world, pos, blockState, fluidState);
            }
        };

        world.explode(null, DamageSource.badRespawnPointExplosion(), explosionBehavior, (double) explodedPos.getX() + 0.5D, (double) explodedPos.getY() + 0.5D, (double) explodedPos.getZ() + 0.5D, 5.0F, false, Explosion.BlockInteraction.DESTROY);
    }

    private static boolean hasStillWater(BlockPos pos, Level world) {
        FluidState fluidState = world.getFluidState(pos);
        if (!fluidState.is(FluidTags.WATER)) {
            return false;
        } else if (fluidState.isSource()) {
            return true;
        } else {
            float f = (float)fluidState.getAmount();
            if (f < 2.0F) {
                return false;
            } else {
                FluidState fluidState2 = world.getFluidState(pos.below());
                return !fluidState2.is(FluidTags.WATER);
            }
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHARGES);
    }

    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    public static int getLightLevel(BlockState state, int maxLevel) {
        return state.getValue(CHARGES) == 0 ? 0 : 7;
    }

    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return Mth.floor((float)(state.getValue(CHARGES)) / 4.0F * (float)15);
    }

    public static Optional<Vec3> findRespawnPosition(EntityType<?> entity, CollisionGetter world, BlockPos pos) {
        Optional<Vec3> optional = findRespawnPosition(entity, world, pos, true);
        return optional.isPresent() ? optional : findRespawnPosition(entity, world, pos, false);
    }

    private static Optional<Vec3> findRespawnPosition(EntityType<?> entity, CollisionGetter world, BlockPos pos, boolean bl) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        UnmodifiableIterator var5 = VALID_SPAWN_OFFSETS.iterator();

        Vec3 vec3d;
        do {
            if (!var5.hasNext()) {
                return Optional.empty();
            }

            Vec3i vec3i = (Vec3i)var5.next();
            mutable.set(pos).move(vec3i);
            vec3d = DismountHelper.findSafeDismountLocation(entity, world, mutable, bl);
        } while(vec3d == null);

        return Optional.of(vec3d);
    }

    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    static {
        DispenserBlock.registerBehavior(Items.ENDER_PEARL, new OptionalDispenseItemBehavior() {
            public ItemStack execute(BlockSource pointer, ItemStack stack) {
                Direction direction = pointer.getBlockState().getValue(DispenserBlock.FACING);
                BlockPos blockPos = pointer.getPos().relative(direction);
                Level world = pointer.getLevel();
                BlockState blockState = world.getBlockState(blockPos);
                this.setSuccess(true);
                if (blockState.is(EndlessVoidBlocks.ENDERLOCK)) {
                    if (blockState.getValue(EnderlockBlock.CHARGES) != 4) {
                        EnderlockBlock.charge(world, blockPos, blockState);
                        stack.shrink(1);
                    } else {
                        this.setSuccess(false);
                    }

                    return stack;
                } else {
                    return super.execute(pointer, stack);
                }
            }
        });
    }
}

