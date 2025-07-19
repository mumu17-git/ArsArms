package com.mumu17.arsarms.util;

import net.minecraft.world.item.ItemStack;

public interface ModernKineticGunItemAccess {

    void setReloadAmoData(ItemStack gun, boolean isArsMode);

    ArsArmsReloadAmmoData getReloadAmoData(ItemStack gun);
}
