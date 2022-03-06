package azonix.endlessvoid;

import net.minecraft.resources.ResourceLocation;

import static azonix.endlessvoid.Constants.MOD_ID;

public class EndlessVoidCommon {

    public static ResourceLocation createLocation(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
    public static boolean respawnAfterCredits = false;
}