package com.mumu17.arsarms.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PlayerAmmoConsumer {

    private static Player player;
    private static ItemStack offhand;

    public static void setOffhand(ItemStack stack) {
        offhand = stack;
    }

    public static void setPlayer(Player p) {
        player = p;
    }

    public static ItemStack getOffHand() {
        return offhand;
    }

    public static Player getPlayer() {
        return player;
    }

    public static void clearOffhand() {
        offhand = ItemStack.EMPTY;
    }

    public static void clearPlayer() {
        player = null;
    }
}

