package com.mumu17.arsarms.util;

import net.minecraft.world.item.ItemStack;

public class PlayerAmmoConsumer {

    private static ItemStack offhand;

    public static void set(ItemStack stack) {
        offhand = stack;
    }

    public static ItemStack getOffHand() {
        return offhand;
    }

    public static void clear() {
        offhand = ItemStack.EMPTY;
    }
}

