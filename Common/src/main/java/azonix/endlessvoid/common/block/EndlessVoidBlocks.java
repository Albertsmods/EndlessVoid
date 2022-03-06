package azonix.endlessvoid.common.block;

import azonix.endlessvoid.util.RegistryObject;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class EndlessVoidBlocks {
    public static final List<RegistryObject<Block>> BLOCKS = new ArrayList<>();

    public static final Block VOIDSTONE = createToolRequiredBlock("voidstone", Material.STONE, MaterialColor.COLOR_YELLOW, 3.0F, 6.0F, SoundType.DEEPSLATE);
    public static final Block COBBLED_VOIDSTONE = createToolRequiredBlock("cobbled_voidstone", Material.STONE, MaterialColor.COLOR_YELLOW, 3.0F, 6.0F, SoundType.DEEPSLATE);
    public static final Block ENDERLOCK = createEnderlockBlock("enderlock");
    
    static Block createToolRequiredBlock(String id, Material material, MaterialColor materialColor, float hardness, float resistance, SoundType soundType) {
        Block createBlock = new Block(BlockBehaviour.Properties.of(material, materialColor).requiresCorrectToolForDrops().strength(hardness, resistance).sound(soundType));
        createBlock(createBlock, id);
        return createBlock;
    }

    static Block createEnderlockBlock(String id) {
        Block createBlock = new EnderlockBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN).requiresCorrectToolForDrops().strength(50.0F, 1200.0F).lightLevel(($$0x) -> {
            return EnderlockBlock.getScaledChargeLevel($$0x, 15);
        }));
        createBlock(createBlock, id);
        return createBlock;
    }

    public static Block createBlock(Block block, String id) {
        BLOCKS.add(new RegistryObject<>(block, id));
        return block;
    }

    public static Collection<RegistryObject<Block>> bootStrap() {
        return BLOCKS;
    }
}
