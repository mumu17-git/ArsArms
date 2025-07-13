package com.mumu17.arsarms.mixin.tacz;

import com.mumu17.arsarms.util.PlayerAmmoConsumer;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModernKineticGunScriptAPI.class)
public class ModernKineticGunScriptAPIMixin {
    @Shadow(remap = false) private LivingEntity shooter;

    @Inject(method = "consumeAmmoFromPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getCapability(Lnet/minecraftforge/common/capabilities/Capability;Lnet/minecraft/core/Direction;)Lnet/minecraftforge/common/util/LazyOptional;"), remap = false)
    public void consumeAmmoFromPlayer(CallbackInfoReturnable<Integer> cir) {
        PlayerAmmoConsumer.set(shooter.getOffhandItem());
    }
}
