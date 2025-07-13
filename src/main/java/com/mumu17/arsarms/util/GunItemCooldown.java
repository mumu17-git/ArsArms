package com.mumu17.arsarms.util;

import net.minecraft.world.item.ItemStack;

public interface GunItemCooldown {

    void setLastTimestamp(ItemStack gunItem, long timestamp);

    long getLastTimestamp(ItemStack gunItem);
}
