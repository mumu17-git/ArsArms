package com.mumu17.arsarms.util;

import com.mumu17.castlib.util.IProjectileDataProvider;
import com.tacz.guns.entity.EntityKineticBullet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

public class ArsArmsProjectileProvider implements IProjectileDataProvider {
    @Override
    public boolean isEnabled(LivingEntity entity) {
        return getProjectile(entity).isEnabled();
    }

    @Override
    public Entity getTargetEntity(LivingEntity entity) {
        return getProjectile(entity).getTargetEntity();
    }

    @Override
    public InteractionHand getHand(LivingEntity entity) {
        return getProjectile(entity).getHand();
    }

    @Override
    public BlockHitResult getBlockHitResult(LivingEntity entity) {
        return getProjectile(entity).getBlockHitResult();
    }

    @Override
    public ItemStack getCurrentGun(LivingEntity entity) {
        return getProjectile(entity).getCurrentGun();
    }

    public ArsArmsProjectileData getProjectile(LivingEntity entity) {
        Entity projectileEntity = ArsArmsProjectileData.getProjectileEntityFromPlayer(entity);
        if (projectileEntity instanceof EntityKineticBullet) {
            return ArsArmsProjectileData.getProjectileData(projectileEntity);
        }
        return null;
    }
}
