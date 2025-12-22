package com.mumu17.arsarms.util;

import com.mumu17.armslib.util.GunItemNbt;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.item.AmmoBoxItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ArsArmsReloadArsModeSettings {

    public static boolean canActive(ItemStack currentGunItem, ItemStack curiosAmmoBox, LivingEntity player) {
        if (curiosAmmoBox.getItem() instanceof AmmoBoxItem && currentGunItem.getItem() instanceof IGun iGun) {
            GunItemNbt access = (GunItemNbt) iGun;
            if (access.getIsIronsMode(currentGunItem))
                return false;

            if (curiosAmmoBox.hasTag() && curiosAmmoBox.getOrCreateTag().contains("ars_nouveau:reactive_caster") &&
                    curiosAmmoBox.getOrCreateTag().contains("Enchantments")) {
                ListTag enchantments = curiosAmmoBox.getOrCreateTag().getList("Enchantments", Tag.TAG_COMPOUND);
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
        return false;
    }

    public static void setActive(ItemStack currentGunItem, ItemStack curiosAmmoBox, LivingEntity player) {
        GunItemNbt access = (GunItemNbt) currentGunItem.getItem();
        access.setIsArsMode(currentGunItem, true);
        access.setOwner(currentGunItem, player);
        access.setInteractionHand(currentGunItem, ArsArmsCuriosUtil.getCuriosSlotFromAmmoBox(player, curiosAmmoBox));
    }

    public static void setInActive(ItemStack currentGunItem, @Nullable ItemStack curiosAmmoBox, @Nullable LivingEntity player) {
        GunItemNbt access = (GunItemNbt) currentGunItem.getItem();
        access.setIsArsMode(currentGunItem, false);
    }
}
