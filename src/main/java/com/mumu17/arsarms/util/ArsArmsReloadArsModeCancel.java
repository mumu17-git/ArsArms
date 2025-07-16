package com.mumu17.arsarms.util;

import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.item.ModernKineticGunItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class ArsArmsReloadArsModeCancel {

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
                    GunItemCooldown gunItemCooldown = (GunItemCooldown) modernKineticGunItem;
                    gunItemCooldown.setLastAmmoCount(currentGunItem, 0);
                }
            }
            access.setReloadAmoData(currentGunItem, flag00);
        }
    }
}
