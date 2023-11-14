package net.fabricmc.quietfishing.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMix {


    @Inject(method = "onPlaySound", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/sound/SoundCategory;FFJ)V"), cancellable = true)
    public void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        if (packet.getSound().value() == SoundEvents.ENTITY_FISHING_BOBBER_SPLASH) {
            double distance = 0.18d;
            var bobbers = client.world.getEntitiesByType(EntityType.FISHING_BOBBER, new Box(packet.getX() - distance, packet.getY() - distance, packet.getZ() - distance, packet.getX() + distance, packet.getY() + distance, packet.getZ() + distance), Entity::isAlive);
            if (bobbers.isEmpty()) return;

            for (var bobberEntity : bobbers) {
                if (!(bobberEntity.getPlayerOwner() instanceof OtherClientPlayerEntity)) {
                    return;
                }
            }
            ci.cancel();
        }
    }


}
