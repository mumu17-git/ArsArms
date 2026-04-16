package com.mumu17.arsarms.util;

import com.mumu17.arsarms.network.RequestSyncChargedManaMessage;
import com.tacz.guns.api.item.IGun;
import net.minecraft.world.item.ItemStack;

public class GunTags {

    public static void addMana(ItemStack stack, int mana) {
        setMana(stack, getMana(stack) + mana);
    }

    public static void setMana(ItemStack stack, int mana) {
        if (!isTargetItem(stack) || mana < 0) return;
        stack.getOrCreateTag().putInt(RequestSyncChargedManaMessage.MANA, mana);
    }

    public static int getMana(ItemStack stack) {
        return containsManaTag(stack) ? stack.getOrCreateTag().getInt(RequestSyncChargedManaMessage.MANA) : 0;
    }

    public static boolean containsManaTag(ItemStack stack) {
        return isTargetItem(stack) && stack.getOrCreateTag().contains(RequestSyncChargedManaMessage.MANA);
    }

    public static boolean isTargetItem(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getItem() instanceof IGun;
    }
}
