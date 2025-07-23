package com.mumu17.arsarms.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ModernKineticGunItemAccess {

    void setReloadAmoData(ItemStack gun, boolean isArsMode);

    ArsArmsReloadAmmoData getReloadAmoData(ItemStack gun);

    void setOwner(ItemStack gun, LivingEntity owner);

    LivingEntity getOwner(ItemStack gun);
}
