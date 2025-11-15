package com.mumu17.arsarms.util;

import com.mumu17.arscurios.util.ArsCuriosInventoryHelper;
import com.mumu17.arscurios.util.ArsCuriosLivingEntity;
import com.mumu17.arscurios.util.ExtendedHand;
import com.tacz.guns.item.AmmoBoxItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ArsArmsCuriosUtil {
    public static ExtendedHand getCuriosSlotFromGun(Player player, ItemStack gunStack) {
        for (ExtendedHand extendedHand : ExtendedHand.values()) {
            ItemStack curiosStack = ArsCuriosInventoryHelper.getCuriosInventoryItem(player, extendedHand.getSlotName());
            if (curiosStack.getItem() instanceof AmmoBoxItem iAmmoBox) {
                if(ArsArmsReloadArsModeActive.active(gunStack, curiosStack, player, false)) {
                    ArsCuriosLivingEntity.setPlayerExtendedHand(player, extendedHand);
                    boolean isAmmoBoxOfGun = iAmmoBox.isAmmoBoxOfGun(gunStack, curiosStack);
                    ArsArmsReloadArsModeCancel.remove(gunStack, player);
                    if (isAmmoBoxOfGun) {
                        return extendedHand;
                    }
                }
            }
        }
        return ExtendedHand.OFF_HAND;
    }
}
