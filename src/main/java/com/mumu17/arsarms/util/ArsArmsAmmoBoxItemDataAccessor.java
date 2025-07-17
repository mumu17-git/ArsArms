package com.mumu17.arsarms.util;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.nbt.AmmoBoxItemDataAccessor;
import com.tacz.guns.item.ModernKineticGunItem;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class ArsArmsAmmoBoxItemDataAccessor implements AmmoBoxItemDataAccessor {
    public boolean isAmmoBoxOfGun(ItemStack gun, ItemStack ammoBox, boolean isFirst) {
        Item var5 = gun.getItem();
        if (var5 instanceof IGun iGun) {
            var5 = ammoBox.getItem();
            if (var5 instanceof IAmmoBox iAmmoBox) {
                boolean flag00 = false;
                boolean flag01 = false;
                Tag gunTag = null;
                Tag ammoBoxTag = null;

                if (ammoBox.hasTag() && ammoBox.getOrCreateTag().contains("ars_nouveau:reactive_caster")) {
                    flag00 = true;
                    ammoBoxTag = ammoBox.getOrCreateTag().get("ars_nouveau:reactive_caster");
                }

                if (gun.hasTag() && gun.getOrCreateTag().contains("ars_nouveau:reactive_caster")) {
                    flag01 = true;
                    gunTag = gun.getOrCreateTag().get("ars_nouveau:reactive_caster");
                }

                if (isFirst) {
                    if (flag00) {
                        if (flag01 && ammoBoxTag != null) {
                            if (ammoBoxTag.equals(gunTag)) {
                                ((ModernKineticGunItemAccess) iGun).setReloadAmoData(gun, true);
                            } else {
                                return false;
                            }
                        } else {
                            ((ModernKineticGunItemAccess) iGun).setReloadAmoData(gun, true);
                        }
                    }
                } else {
                    ArsArmsReloadAmmoData reloadAmmoData = ((ModernKineticGunItemAccess) iGun).getReloadAmoData(gun);
                    if (reloadAmmoData != null) {
                        if (flag00 != reloadAmmoData.isArsMode()) {
                            return false;
                        }
                    }
                    if (flag01) {
                        if (!Objects.requireNonNull(ammoBoxTag).equals(gunTag)) {
                            return false;
                        }
                    }
                }

                if (this.isAllTypeCreative(ammoBox)) {
                    return true;
                }

                ResourceLocation ammoId = iAmmoBox.getAmmoId(ammoBox);
                if (ammoId.equals(DefaultAssets.EMPTY_AMMO_ID)) {
                    return false;
                }


                if (PlayerAmmoConsumer.getOffHand().getItem() instanceof IAmmoBox offhandIAmmoBox) {
                    ResourceLocation offhandAmmoId = offhandIAmmoBox.getAmmoId(PlayerAmmoConsumer.getOffHand());
                    if (!offhandAmmoId.equals(DefaultAssets.EMPTY_AMMO_ID)) {
                        if (!ammoId.equals(offhandAmmoId)) {
                            return false;
                        }
                    }
                }

                if (gun.getItem() instanceof ModernKineticGunItem modernKineticGunItem) {
                    CommonGunIndex index = TimelessAPI.getCommonGunIndex(modernKineticGunItem.getGunId(gun)).orElse(null);
                    if (index != null) {
                        GunData gunData = index.getGunData();
                        ResourceLocation gunAmmoId = gunData.getAmmoId();
                        if (!ammoId.equals(gunAmmoId)) {
                            return false;
                        }
                    }
                }

                ResourceLocation gunId = iGun.getGunId(gun);
                return (Boolean) TimelessAPI.getCommonGunIndex(gunId).map((gunIndex) -> gunIndex.getGunData().getAmmoId().equals(ammoId)).orElse(false);
            }
        }

        return false;
    }
}
