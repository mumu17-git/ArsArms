package com.mumu17.arsarms.util;

import com.mumu17.castlib.util.IArmsNbtProvider;
import net.minecraft.world.item.ItemStack;

public class ArsArmsGunCooldownProvider implements IArmsNbtProvider {
    @Override
    public float getArmsDamage(ItemStack gunItem) {
        GunItemNbt gun = (GunItemNbt) gunItem.getItem();
        return gun.getGunDamage(gunItem);
    }
}

