package com.mumu17.arsarms.util;

import net.minecraft.world.item.ItemStack;

public interface ModernKineticGunItemAccess {

    void setReloadAmoData(ItemStack stack, boolean isArsMode);

    ArsArmsReloadAmmoData getReloadAmoData(ItemStack stack);
}
