package azonix.endlessvoid;

import azonix.endlessvoid.common.block.EndlessVoidBlocks;
import azonix.endlessvoid.common.item.EndlessVoidItems;
import azonix.endlessvoid.util.RegistryObject;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Collection;
import java.util.function.Supplier;

@Mod(Constants.MOD_ID)
public class EndlessVoidForge {

    public EndlessVoidForge() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        bootStrap(eventBus);
    }
    
    private void bootStrap(IEventBus eventBus) {
        register(Block.class, eventBus, () -> EndlessVoidBlocks.bootStrap());
        register(Item.class, eventBus, () -> EndlessVoidItems.bootStrap());
    }

    @SuppressWarnings("rawtypes")
    private <T extends IForgeRegistryEntry<T>> void register(Class clazz, IEventBus eventBus, Supplier<Collection<RegistryObject<T>>> registryObjectsSupplier) {
        eventBus.addGenericListener(clazz, (RegistryEvent.Register<T> event) -> {
            Collection<RegistryObject<T>> registryObjects = registryObjectsSupplier.get();
            IForgeRegistry<T> registry = event.getRegistry();
            for (RegistryObject<T> registryObject : registryObjects) {
                registryObject.object().setRegistryName(EndlessVoidCommon.createLocation(registryObject.id()));
                registry.register(registryObject.object());
            }
        });
    }
}