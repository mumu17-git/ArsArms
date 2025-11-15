package com.mumu17.arsarms.util;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import com.mumu17.armslib.util.GunItemNbt;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.item.ModernKineticGunItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ArsArmsReloadArsModeActive {

    public static boolean active(ItemStack currentGunItem, ItemStack curiosAmmoBox, LivingEntity player, boolean expendMana) {
        boolean flag00 = false;
        if (curiosAmmoBox.getItem() instanceof AmmoBoxItem && currentGunItem.getItem() instanceof IGun iGun) {
            GunItemNbt access = (GunItemNbt) iGun;
            if (!access.getIsIronsMode(currentGunItem)) {
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
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    enchantmentsGunItem.add(enchantmentTag);
                                    currentGunItem.getOrCreateTag().put("Enchantments", enchantmentsGunItem);
                                    flag00 = true;
                                    access.setIsArsMode(currentGunItem, true);
                                    break;
                                }
                            }
                        }
                    }
                    if (ammoBoxTag != null) {
                        currentGunItem.getOrCreateTag().put("ars_nouveau:reactive_caster", ammoBoxTag);
                    }

                    if (expendMana && flag00) {
                        int chargedManaCount = ArsArmsAmmoBox.getChargedManaCount(curiosAmmoBox);
                        ReactiveCaster casterData = new ReactiveCaster(curiosAmmoBox);
                        Spell spell = casterData.getSpell();

                        int cost = spell.getCost();

                        int reloadAmmoCount = 0;
                        if (currentGunItem.getItem() instanceof ModernKineticGunItem) {
                            reloadAmmoCount = 1;
                        }
                        curiosAmmoBox.getOrCreateTag().putInt("Mana", chargedManaCount - cost * reloadAmmoCount);
                    }
                }
            } else {
                ArsArmsReloadArsModeCancel.remove(currentGunItem, player);
            }
        }
        return flag00;
    }
}