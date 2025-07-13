package com.mumu17.arsarms.mixin;

import com.mumu17.arsarms.util.ArsArmsProjectileData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityHitResult.class)
public class EntityHitResultMixin {
    @Inject(method = "getEntity", at = @At(value = "HEAD"), cancellable = true)
    public void getEntity(CallbackInfoReturnable<Entity> cir) {
        if (ArsArmsProjectileData.isEnabled()) {
            Entity entity = ArsArmsProjectileData.getTargetEntity();
            InteractionHand hand = ArsArmsProjectileData.getHand();
            if (hand == InteractionHand.OFF_HAND) {
                if (entity != null) {
                    cir.setReturnValue(entity);
                }
            }
        }
    }
}
