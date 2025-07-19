package com.mumu17.arsarms.util;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.item.ModernKineticGunItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class ArsArmsReloadArsModeActive {
    public static void active(ItemStack currentGunItem, ItemStack offhand, boolean expendMana) {
        // ArsArmsReloadAmmoData reloadAmmoData = access.getReloadAmoData(currentGunItem);
        boolean flag00 = false;
        if (offhand.getItem() instanceof AmmoBoxItem) {
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
                                                break;
                                            }
                                        }
                                    }
                                }
                                enchantmentsGunItem.add(enchantmentTag);
                                currentGunItem.getOrCreateTag().put("Enchantments", enchantmentsGunItem);
                                flag00 = true;
                                break;
                            }
                        }
                    }
                }
                if (ammoBoxTag != null) {
                    currentGunItem.getOrCreateTag().put("ars_nouveau:reactive_caster", ammoBoxTag);
                }

                if (expendMana) {
                    int chargedManaCount = ArsArmsAmmoBox.getChargedManaCount(offhand);
                    ReactiveCaster casterData = new ReactiveCaster(offhand);
                    Spell spell = casterData.getSpell();

                    int cost = spell.getCost();

                    int reloadAmmoCount = 0;
                    if (currentGunItem.getItem() instanceof ModernKineticGunItem) {
                        reloadAmmoCount = 1;
                    }
                    offhand.getOrCreateTag().putInt("Mana", chargedManaCount - cost * reloadAmmoCount);
                }
            }
        }
        ModernKineticGunItemAccess access = (ModernKineticGunItemAccess) currentGunItem.getItem();
        access.setReloadAmoData(currentGunItem, flag00);
    }
}