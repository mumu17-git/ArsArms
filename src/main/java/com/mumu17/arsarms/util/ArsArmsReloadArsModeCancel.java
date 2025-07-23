package com.mumu17.arsarms.util;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.item.ModernKineticGunItem;
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

    public static void cancel(ItemStack currentGunItem, ItemStack offhand) {
        ModernKineticGunItemAccess access = (ModernKineticGunItemAccess) currentGunItem.getItem();
        if (offhand.getItem() instanceof AmmoBoxItem) {
            boolean flag00 = false;
            if (offhand.hasTag() && offhand.getOrCreateTag().contains("ars_nouveau:reactive_caster")) {
                Tag ammoBoxTag = offhand.getOrCreateTag().get("ars_nouveau:reactive_caster");
                if (offhand.getOrCreateTag().contains("Enchantments")) {
                    ListTag enchantments = offhand.getOrCreateTag().getList("Enchantments", Tag.TAG_COMPOUND);
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

                if (currentGunItem.getItem() instanceof ModernKineticGunItem modernKineticGunItem) {
                    GunItemNbt gunItemCooldown = (GunItemNbt) modernKineticGunItem;
                    gunItemCooldown.setLastAmmoCount(currentGunItem, 0);
                }
            }
            access.setReloadAmoData(currentGunItem, flag00);
        }
    }

    public static void remove(ItemStack gunItem, LivingEntity shooter) {
        if (gunItem != ItemStack.EMPTY && gunItem.getItem() instanceof ModernKineticGunItem modernKineticGunItem) {
            boolean useInventoryAmmo = modernKineticGunItem.useInventoryAmmo(gunItem);
            if(shooter instanceof Player player) {
                CommonGunIndex index = TimelessAPI.getCommonGunIndex(modernKineticGunItem.getGunId(gunItem)).orElse(null);
                if (index != null) {
                    GunData gunData = index.getGunData();
                    if (gunData != null) {
                        if (gunItem.getItem() instanceof IGun iGun) {
                            int ammoCount = useInventoryAmmo ? ArsArmsAmmoUtil.handleInventoryAmmo(gunItem, player.getInventory()) + (iGun.hasBulletInBarrel(gunItem) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0) :
                                    iGun.getCurrentAmmoCount(gunItem) + (iGun.hasBulletInBarrel(gunItem) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0);
                            ammoCount = Math.min(ammoCount, MAX_AMMO_COUNT);
                            ModernKineticGunItemAccess access = (ModernKineticGunItemAccess) iGun;
                            ArsArmsReloadAmmoData reloadAmmoData = access.getReloadAmoData(gunItem);
                            if (reloadAmmoData != null) {
                                boolean isArsMode = reloadAmmoData.isArsMode();
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
                                    access.setReloadAmoData(gunItem, false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
