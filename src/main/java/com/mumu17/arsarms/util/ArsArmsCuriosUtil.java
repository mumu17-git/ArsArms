package com.mumu17.arsarms.util;

import com.mumu17.arscurios.util.ExtendedHand;
import com.tacz.guns.item.ModernKineticGunItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class ArsArmsCuriosUtil {
    public static ExtendedHand getCuriosSlotFromGun(Player player, ItemStack gunStack) {
        ExtendedHand hand = ExtendedHand.OFF_HAND;
        for (int i = 0; i < ExtendedHand.values().length; i++) {
            LazyOptional<ICuriosItemHandler> curiosItemHandlerLazyOptional = CuriosApi.getCuriosInventory(player);
            ICuriosItemHandler curiosItemHandler = curiosItemHandlerLazyOptional.orElse(null);
            if (curiosItemHandler.getCurios().containsKey(ExtendedHand.values()[i].getSlotName())) {
                ItemStack curiosStack = curiosItemHandler.getCurios().get(ExtendedHand.values()[i].getSlotName()).getStacks().getStackInSlot(0);
                if (curiosStack.getItem() instanceof ModernKineticGunItem) {
                    hand = ExtendedHand.values()[i];
                    break;
                }
            }
        }
        return hand;
    }
}
