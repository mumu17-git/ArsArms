package com.mumu17.arsarms.util;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ArsArmsReloadArsModeCancel {

    private static final int MAX_AMMO_COUNT = 9999;

    public static void cancel(ItemStack currentGunItem, ItemStack curiosAmmoBox) {
        GunItemNbt access = (GunItemNbt) currentGunItem.getItem();
        if (curiosAmmoBox.getItem() instanceof AmmoBoxItem) {
            if (curiosAmmoBox.hasTag() && curiosAmmoBox.getOrCreateTag().contains("ars_nouveau:reactive_caster")) {
                Tag ammoBoxTag = curiosAmmoBox.getOrCreateTag().get("ars_nouveau:reactive_caster");
                if (curiosAmmoBox.getOrCreateTag().contains("Enchantments")) {
                    ListTag enchantments = curiosAmmoBox.getOrCreateTag().getList("Enchantments", Tag.TAG_COMPOUND);
                    if (!enchantments.isEmpty()) {
                        for (int i = 0; i < enchantments.size(); i++) {
                            CompoundTag enchantmentTag = enchantments.getCompound(i);
                            String enchantmentId = enchantmentTag.getString("id");
                            if ("ars_nouveau:reactive".equals(enchantmentId)) {
                                ListTag enchantmentsGunItem = new ListTag();
                                if (currentGunItem.hasTag() && currentGunItem.getOrCreateTag().contains("Enchantments")) {
                                    enchantmentsGunItem = currentGunItem.getOrCreateTag().getList("Enchantments", Tag.TAG_COMPOUND);
                                    if (!enchantmentsGunItem.isEmpty()) {
                                        for (int j = 0; j < enchantmentsGunItem.size(); j++) {
                                            CompoundTag enchantmentTagGunItem = enchantmentsGunItem.getCompound(j);
                                            String enchantmentIdGunItem = enchantmentTagGunItem.getString("id");
                                            if ("ars_nouveau:reactive".equals(enchantmentIdGunItem)) {
                                                enchantmentsGunItem.remove(j);
                                                currentGunItem.getOrCreateTag().put("Enchantments", enchantmentsGunItem);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                currentGunItem.getOrCreateTag().remove("ars_nouveau:reactive_caster");

                if (currentGunItem.getItem() instanceof IGun iGun) {
                    GunItemNbt gunItemCooldown = (GunItemNbt) iGun;
                    gunItemCooldown.setLastAmmoCount(currentGunItem, 0);
                }
                access.setIsArsMode(currentGunItem, false);
            }
        }
    }

    public static void remove(ItemStack gunItem, LivingEntity shooter) {
        if (gunItem != ItemStack.EMPTY && gunItem.getItem() instanceof IGun iGun) {
            boolean useInventoryAmmo = iGun.useInventoryAmmo(gunItem);
            if(shooter instanceof Player player) {
                CommonGunIndex index = TimelessAPI.getCommonGunIndex(iGun.getGunId(gunItem)).orElse(null);
                if (index != null) {
                    GunData gunData = index.getGunData();
                    if (gunData != null) {
                        int ammoCount = useInventoryAmmo ? ArsArmsAmmoUtil.handleInventoryAmmo(gunItem, player.getInventory()) + (iGun.hasBulletInBarrel(gunItem) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0) :
                                iGun.getCurrentAmmoCount(gunItem) + (iGun.hasBulletInBarrel(gunItem) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0);
                        ammoCount = Math.min(ammoCount, MAX_AMMO_COUNT);
                        GunItemNbt access = (GunItemNbt) iGun;
                        boolean isArsMode = access.getIsArsMode(gunItem);
                        if (isArsMode) {
                            if (gunItem.hasTag() && gunItem.getOrCreateTag().contains("ars_nouveau:reactive_caster")) {
                                if (gunItem.getOrCreateTag().contains("Enchantments")) {
                                    ListTag enchantments = gunItem.getOrCreateTag().getList("Enchantments", Tag.TAG_COMPOUND);
                                    if (!enchantments.isEmpty()) {
                                        for (int i = 0; i < enchantments.size(); i++) {
                                            CompoundTag enchantmentTag = enchantments.getCompound(i);
                                            String enchantmentId = enchantmentTag.getString("id");
                                            if ("ars_nouveau:reactive".equals(enchantmentId)) {
                                                enchantments.remove(i);
                                                gunItem.getOrCreateTag().put("Enchantments", enchantments);
                                                break;
                                            }
                                        }
                                    }
                                }
                                gunItem.getOrCreateTag().remove("ars_nouveau:reactive_caster");
                            }
                            access.setIsArsMode(gunItem, false);
                        }
                    }
                }
            }
        }
    }
}
