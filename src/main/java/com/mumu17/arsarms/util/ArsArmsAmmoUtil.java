package com.mumu17.arsarms.util;

import com.mumu17.arscurios.util.ArsCuriosInventoryHelper;
import com.mumu17.arscurios.util.ExtendedHand;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.item.AmmoItem;
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

        for (int i = 0; i < ExtendedHand.values().length; i++) {
            ItemStack curiosStack = ArsCuriosInventoryHelper.getCuriosInventoryItem(inventory.player, ExtendedHand.values()[i].getSlotName());
            int tmp = handleInventoryAmmo(stack, null, curiosStack, cacheInventoryAmmoCount, -1);
            if (tmp != cacheInventoryAmmoCount) {
                cacheInventoryAmmoCount = tmp;
                break;
            }
        }

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            cacheInventoryAmmoCount = handleInventoryAmmo(stack, inventory, null, cacheInventoryAmmoCount, i);
            if (cacheInventoryAmmoCount >= 9999) {
                break;
            }
        }
        return cacheInventoryAmmoCount;
    }

    public static int handleInventoryAmmo(ItemStack stack, Inventory inventory, ItemStack curiosStack, int cacheInventoryAmmoCount, int i) {
        if ((inventory != null) || (curiosStack != null)) {
            ItemStack inventoryItem = (inventory != null ? inventory.getItem(i) : curiosStack);
            if (inventoryItem.getItem() instanceof AmmoItem iAmmo) {
                if (iAmmo.isAmmoOfGun(stack, inventoryItem)) {
                    cacheInventoryAmmoCount += inventoryItem.getCount();
                }
            }
            if (inventoryItem.getItem() instanceof AmmoBoxItem iAmmoBox) {
                if (iAmmoBox.isAmmoBoxOfGun(stack, inventoryItem)) {
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
                                    ((GunItemNbt) iGun).setIsArsMode(gun, true);
                                }
                            } else {
                                return false;
                            }
                        } else {
                            if (!checkOnly) {
                                ((GunItemNbt) iGun).setIsArsMode(gun, true);
                            }
                        }
                    }
                } else {
                    if (flag00 != ((GunItemNbt) iGun).getIsArsMode(gun)) {
                        return false;
                    }
                    if (flag01) {
                        if (!Objects.requireNonNull(ammoBoxTag).equals(gunTag)) {
                            return false;
                        }
                    }
                }
                return true;
            } else if (var5 instanceof IAmmo) {
                return !((GunItemNbt) iGun).getIsArsMode(gun);
            }
        }
        return false;
    }
}
