package com.mumu17.arsarms.util;

import com.mumu17.castlib.util.IProjectileDataProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

public class ArsArmsProjectileProvider implements IProjectileDataProvider {
    @Override
    public boolean isEnabled(LivingEntity entity) {
        return ArsArmsProjectileData.isEnabled();
    }

    @Override
    public Entity getTargetEntity(LivingEntity entity) {
        return ArsArmsProjectileData.getTargetEntity();
    }

    @Override
    public InteractionHand getHand(LivingEntity entity) {
        return ArsArmsProjectileData.getHand();
    }

    @Override
    public BlockHitResult getBlockHitResult(LivingEntity entity) {
        return ArsArmsProjectileData.getBlockHitResult();
    }

    @Override
    public ItemStack getCurrentGun(LivingEntity entity) {
        return ArsArmsProjectileData.getCurrentGun();
    }
}
