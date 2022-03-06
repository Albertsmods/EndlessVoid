package azonix.endlessvoid.common.block;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class EnderlockBlock extends RespawnAnchorBlock {
    public static final IntegerProperty CHARGE;
    private static final IntegerProperty ENDERLOCK_CHARGES;

    public EnderlockBlock(Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(CHARGE, 0));
    }

    public static boolean canSetSpawn(Level $$0) {
        return $$0.dimensionType().createDragonFight();
    }

    private static boolean canBeCharged(BlockState $$0) {
        return (Integer)$$0.getValue(CHARGE) < 4;
    }

    private static boolean isRespawnFuel(ItemStack $$0) {
        return $$0.is(Items.ENDER_PEARL);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(new Property[]{CHARGE});
    }

    static {
        ENDERLOCK_CHARGES = IntegerProperty.create("charges", 0, 4);
        CHARGE = ENDERLOCK_CHARGES;
    }
}
