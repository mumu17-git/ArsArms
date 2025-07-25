package com.mumu17.arsarms.util;

import com.mumu17.castlib.util.IGunItemCooldownProvider;
import net.minecraft.world.item.ItemStack;

public class ArsArmsGunCooldownProvider implements IGunItemCooldownProvider {
    @Override
    public float getGunDamage(ItemStack gunItem) {
        GunItemNbt gun = (GunItemNbt) gunItem.getItem();
        return gun.getGunDamage(gunItem);
    }
}

