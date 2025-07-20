package com.mumu17.arsarms.util;

import com.mumu17.castlib.util.IProjectileDataProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

public class ArsArmsProjectileProvider implements IProjectileDataProvider {
    @Override
    public boolean isEnabled() {
        return ArsArmsProjectileData.isEnabled();
    }

    @Override
    public Entity getTargetEntity() {
        return ArsArmsProjectileData.getTargetEntity();
    }

    @Override
    public InteractionHand getHand() {
        return ArsArmsProjectileData.getHand();
    }

    @Override
    public BlockHitResult getBlockHitResult() {
        return ArsArmsProjectileData.getBlockHitResult();
    }

    @Override
    public ItemStack getCurrentGun() {
        return ArsArmsProjectileData.getCurrentGun();
    }
}
