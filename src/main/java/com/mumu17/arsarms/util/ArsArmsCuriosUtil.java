package com.mumu17.arsarms.util;

import com.mumu17.armslib.util.GunItemNbt;
import com.mumu17.arsarms.ArsArms;
import com.mumu17.arscurios.util.ArsCuriosInventoryHelper;
import com.mumu17.arscurios.util.InteractionHandUtil;
import com.tacz.guns.item.AmmoBoxItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ArsArmsCuriosUtil {
    public static InteractionHand getCuriosSlotFromGun(LivingEntity player, ItemStack gunStack) {
        GunItemNbt access = (GunItemNbt) gunStack.getItem();
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack curiosStack = ArsCuriosInventoryHelper.getCuriosInventoryItem(player, InteractionHandUtil.getSlotName(hand));
            if (curiosStack.getItem() instanceof AmmoBoxItem iAmmoBox) {
                if(ArsArmsReloadArsModeSettings.canActive(gunStack, curiosStack, player)) {
                    boolean isAmmoBoxOfGun = iAmmoBox.isAmmoBoxOfGun(gunStack, curiosStack);
                    if (isAmmoBoxOfGun) {
                        // ArsArms.LOGGER.debug(hand.name());
                        return hand;
                    }
                }
            }
        }
        return InteractionHand.OFF_HAND;
    }

    public static InteractionHand getCuriosSlotFromAmmoBox(LivingEntity player, ItemStack ammoBoxStack) {
        if (player != null) {
            for (InteractionHand hand : InteractionHand.values()) {
                if (InteractionHandUtil.isAmmoBox(hand)) {
                    ItemStack stack = ArsCuriosInventoryHelper.getCuriosInventoryItem(player, InteractionHandUtil.getSlotName(hand));
                    if (ItemStack.isSameItemSameTags(stack, ammoBoxStack)) {
                        return hand;
                    }
                }
            }
        }
        return InteractionHand.MAIN_HAND;
    }
}
