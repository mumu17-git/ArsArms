package com.mumu17.arsarms.util;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.nbt.AmmoItemDataAccessor;
import com.tacz.guns.item.ModernKineticGunItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ArsArmsAmmoItemDataAccessor implements AmmoItemDataAccessor {

    @Override
    public boolean isAmmoOfGun(ItemStack gun, ItemStack ammo) {
        if (gun.getItem() instanceof ModernKineticGunItem modernKineticGunItem) {
            ModernKineticGunItemAccess access = (ModernKineticGunItemAccess) modernKineticGunItem;
            ArsArmsReloadAmmoData reloadAmmoData = access.getReloadAmoData(gun);
            if (reloadAmmoData.isArsMode()) {
                return false;
            }
        }

        Item var5 = gun.getItem();
        if (var5 instanceof IGun iGun) {
            var5 = ammo.getItem();
            if (var5 instanceof IAmmo iAmmo) {
                ResourceLocation gunId = iGun.getGunId(gun);
                ResourceLocation ammoId = iAmmo.getAmmoId(ammo);
                return (Boolean) TimelessAPI.getCommonGunIndex(gunId).map((gunIndex) -> gunIndex.getGunData().getAmmoId().equals(ammoId)).orElse(false);
            }
        }

        return false;
    }
}
