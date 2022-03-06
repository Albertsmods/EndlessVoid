package azonix.endlessvoid.mixin;

import azonix.endlessvoid.EndlessVoidCommon;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPlayerNetworkHandlerMixin {

    @Inject(method = "handleGameEvent", at = @At("HEAD"))
    public void onStateChange(ClientboundGameEventPacket p, CallbackInfo ci) {

        if (p.getEvent() == ClientboundGameEventPacket.WIN_GAME) {
            EndlessVoidCommon.respawnAfterCredits = true;
        }

    }
}
