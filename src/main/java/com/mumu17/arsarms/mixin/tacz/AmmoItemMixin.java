package com.mumu17.arsarms.mixin.tacz;

import com.mumu17.arsarms.util.GunItemNbt;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.nbt.AmmoItemDataAccessor;
import com.tacz.guns.item.AmmoItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AmmoItem.class)
@Implements(value = @Interface(iface = AmmoItemDataAccessor.class, prefix = "AmmoItemDataAccessor$"))
public class AmmoItemMixin {
    public boolean AmmoItemDataAccessor$isAmmoOfGun(ItemStack gun, ItemStack ammo) {
        if (gun.getItem() instanceof IGun iGun) {
            GunItemNbt access = (GunItemNbt) iGun;
            if (access.getIsArsMode(gun)) {
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
