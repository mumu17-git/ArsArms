package com.mumu17.arsarms.mixin.tacz;

import com.mumu17.arsarms.util.ArsArmsProjectileData;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.util.EntityUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityUtil.class)
public class EntityUtilMixin {
    @Inject(method = "getHitResult", at = @At(value = "TAIL"), remap = false)
    private static void getHitResult(Projectile bulletEntity, Entity entity, Vec3 startVec, Vec3 endVec, CallbackInfoReturnable<EntityKineticBullet.EntityResult> cir) {
//        if (cir.getReturnValue() != null) {
//            ArsArmsProjectileData.setProjectileEntityToPlayer((LivingEntity) bulletEntity.getOwner(), bulletEntity);
//            ArsArmsProjectileData.setProjectileToEntity(entity, bulletEntity);
//        }
    }
}
