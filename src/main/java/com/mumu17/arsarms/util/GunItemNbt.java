package com.mumu17.arsarms.util;

import net.minecraft.world.item.ItemStack;

public interface GunItemNbt {

    void setLastTimestamp(ItemStack gunItem, long timestamp);

    long getLastTimestamp(ItemStack gunItem);

    void setLastAmmoCount(ItemStack gunItem, int ammoCount);

    int getLastAmmoCount(ItemStack gunItem);

    void setGunDamage(ItemStack gunItem, float damage);

    float getGunDamage(ItemStack gunItem);

    void setReloadedSlot(ItemStack gunItem, String slotName);

    String getReloadedSlot(ItemStack gunItem);
}
