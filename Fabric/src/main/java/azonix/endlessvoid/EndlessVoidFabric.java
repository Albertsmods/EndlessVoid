package azonix.endlessvoid;

import azonix.endlessvoid.common.block.EndlessVoidBlocks;
import azonix.endlessvoid.common.item.EndlessVoidItems;
import azonix.endlessvoid.util.RegistryObject;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;

import java.util.Collection;

public class EndlessVoidFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        registryBootStrap();
    }

    private void registryBootStrap() {
        register(Registry.BLOCK, EndlessVoidBlocks.bootStrap());
        register(Registry.ITEM, EndlessVoidItems.bootStrap());
    }

    public static <T> void register(Registry<T> registry, Collection<RegistryObject<T>> objects) {
        for (RegistryObject<T> object : objects) {
            Registry.register(registry, EndlessVoidCommon.createLocation(object.id()), object.object());
        }}
}
