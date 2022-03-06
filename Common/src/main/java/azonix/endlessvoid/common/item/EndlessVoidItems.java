package azonix.endlessvoid.common.item;

import azonix.endlessvoid.EndlessVoidCommon;
import azonix.endlessvoid.common.block.EndlessVoidBlocks;
import azonix.endlessvoid.util.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EndlessVoidItems {

    public static final List<RegistryObject<Item>> ITEMS = new ArrayList<>();

    public static final Item VOIDSTONE = createItem(EndlessVoidBlocks.VOIDSTONE);
    public static final Item COBBLED_VOIDSTONE = createItem(EndlessVoidBlocks.COBBLED_VOIDSTONE);
    public static final Item ENDERLOCK = createItem(EndlessVoidBlocks.ENDERLOCK);

    public static BlockItem createItem(Block block) {
        return createItem(new BlockItem(block, new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)), block);
    }

    public static <T extends Item> T createItem(T item, Block block) {
        ResourceLocation id = Registry.BLOCK.getKey(block);
        if (id == null || id.equals(new ResourceLocation("minecraft:air"))) {
            boolean recovered = false;
            for (RegistryObject<Block> blockRegistryObject : EndlessVoidBlocks.BLOCKS) {
                if (blockRegistryObject.object() == block) {
                    recovered = true;
                    id = EndlessVoidCommon.createLocation(blockRegistryObject.id());
                    break;
                }
}}
        return createItem(item, id.getPath());
    }
    public static <T extends Item> T createItem(T item, String id) {
        ITEMS.add(new RegistryObject<>(item, id));
        return item;
    }

    public static Collection<RegistryObject<Item>> bootStrap() {
        return ITEMS;
    }
}
