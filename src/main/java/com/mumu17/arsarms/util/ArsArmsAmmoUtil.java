package com.mumu17.arsarms.util;

import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.IGun;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class ArsArmsAmmoUtil {

    public static int handleInventoryAmmo(ItemStack stack, Inventory inventory) {
        int cacheInventoryAmmoCount = 0;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack inventoryItem = inventory.getItem(i);
            if (inventoryItem.getItem() instanceof IAmmo iAmmo) {
                ArsArmsAmmoItemDataAccessor accessor = new ArsArmsAmmoItemDataAccessor();
                if (accessor.isAmmoOfGun(stack, inventoryItem)) {
                    cacheInventoryAmmoCount += inventoryItem.getCount();
                }
            }
            if (inventoryItem.getItem() instanceof IAmmoBox iAmmoBox) {
                ArsArmsAmmoBoxItemDataAccessor accessor = new ArsArmsAmmoBoxItemDataAccessor();
                if (accessor.isAmmoBoxOfGun(stack, inventoryItem)) {
                    if (iAmmoBox.isAllTypeCreative(inventoryItem) || iAmmoBox.isCreative(inventoryItem)) {
                        cacheInventoryAmmoCount = 9999;
                        return cacheInventoryAmmoCount;
                    }
                    cacheInventoryAmmoCount += iAmmoBox.getAmmoCount(inventoryItem);

                }
            }
        }
        return cacheInventoryAmmoCount;
    }

    public static boolean isAmmoBoxArsMode(ItemStack ammoBox) {
        if (ammoBox.getItem() instanceof IAmmoBox) {
            if (ammoBox.hasTag() && ammoBox.getOrCreateTag().contains("ars_nouveau:reactive_caster")) {
                Tag ammoBoxTag = ammoBox.getOrCreateTag().get("ars_nouveau:reactive_caster");
                if (ammoBoxTag != null) {
                    if (ammoBox.getOrCreateTag().contains("Enchantments")) {
                        ListTag enchantments = ammoBox.getOrCreateTag().getList("Enchantments", Tag.TAG_COMPOUND);
                        if (!enchantments.isEmpty()) {
                            for (int i = 0; i < enchantments.size(); i++) {
                                CompoundTag enchantmentTag = enchantments.getCompound(i);
                                String enchantmentId = enchantmentTag.getString("id");
                                if ("ars_nouveau:reactive".equals(enchantmentId)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    public static boolean isArsModeFromTag(ItemStack gun, ItemStack ammoBoxOrAmmo, boolean isFirst, boolean checkOnly) {
        Item var5 = gun.getItem();
        if (var5 instanceof IGun iGun) {
            var5 = ammoBoxOrAmmo.getItem();
            if (var5 instanceof IAmmoBox iAmmoBox) {
                boolean flag00 = false;
                boolean flag01 = false;
                Tag gunTag = null;
                Tag ammoBoxTag = null;

                if (ammoBoxOrAmmo.hasTag() && ammoBoxOrAmmo.getOrCreateTag().contains("ars_nouveau:reactive_caster")) {
                    flag00 = true;
                    ammoBoxTag = ammoBoxOrAmmo.getOrCreateTag().get("ars_nouveau:reactive_caster");
                }

                if (gun.hasTag() && gun.getOrCreateTag().contains("ars_nouveau:reactive_caster")) {
                    flag01 = true;
                    gunTag = gun.getOrCreateTag().get("ars_nouveau:reactive_caster");
                }

                if (isFirst) {
                    if (flag00) {
                        if (flag01 && ammoBoxTag != null) {
                            if (ammoBoxTag.equals(gunTag)) {
                                if (!checkOnly) {
                                    ((ModernKineticGunItemAccess) iGun).setReloadAmoData(gun, true);
                                }
                            } else {
                                return false;
                            }
                        } else {
                            if (!checkOnly) {
                                ((ModernKineticGunItemAccess) iGun).setReloadAmoData(gun, true);
                            }
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
                return true;
            } else if (var5 instanceof IAmmo iAmmo) {
                ArsArmsReloadAmmoData reloadAmmoData = ((ModernKineticGunItemAccess) iGun).getReloadAmoData(gun);
                if (reloadAmmoData != null) {
                    if (reloadAmmoData.isArsMode()) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
