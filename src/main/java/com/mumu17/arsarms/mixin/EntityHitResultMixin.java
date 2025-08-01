package com.mumu17.arsarms.mixin;

import com.mumu17.arsarms.util.ArsArmsProjectileData;
import com.mumu17.arscurios.util.ExtendedHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityHitResult.class)
public class EntityHitResultMixin {
    @Shadow @Final private Entity entity;

    @Inject(method = "getEntity", at = @At(value = "HEAD"), cancellable = true)
    public void getEntity(CallbackInfoReturnable<Entity> cir) {
//        Entity projectile = ArsArmsProjectileData.getProjectileFromEntity(this.entity);
//        if (projectile != null) {
//            ArsArmsProjectileData projectileData = ArsArmsProjectileData.getProjectileData(projectile);
//            if (projectileData.isEnabled()) {
//                Entity entity = projectileData.getTargetEntity();
//                ExtendedHand hand = projectileData.getHand();
//                if (hand.isCurios()) {
//                    if (entity != null) {
//                        cir.setReturnValue(entity);
//                        cir.cancel();
//                    }
//                }
//            }
//        }
    }
}
