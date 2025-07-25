package com.mumu17.arsarms.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface GunItemNbt {

    void setLastTimestamp(ItemStack gunItem, long timestamp);

    long getLastTimestamp(ItemStack gunItem);

    void setLastAmmoCount(ItemStack gunItem, int ammoCount);

    int getLastAmmoCount(ItemStack gunItem);

    void setGunDamage(ItemStack gunItem, float damage);

    float getGunDamage(ItemStack gunItem);

    void setIsArsMode(ItemStack gun, boolean isArsMode);

    boolean getIsArsMode(ItemStack gun);

    void setOwner(ItemStack gun, LivingEntity owner);

    LivingEntity getOwner(ItemStack gun);
}
