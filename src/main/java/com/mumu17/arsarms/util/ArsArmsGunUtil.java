package com.mumu17.arsarms.util;

import com.tacz.guns.api.item.IGun;
import net.minecraft.world.item.ItemStack;

public class ArsArmsGunUtil {
    public static void addGunTags(IGun iGun, ItemStack gun, int ammoCount) {
        GunItemNbt access = (GunItemNbt) iGun;
        access.setIsArsMode(gun, false);
        access.setLastAmmoCount(gun, ammoCount);
        access.setGunDamage(gun, 0.0F);
        access.setLastTimestamp(gun, System.currentTimeMillis());
    }
}
