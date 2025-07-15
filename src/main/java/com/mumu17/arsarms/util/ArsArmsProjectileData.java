package com.mumu17.arsarms.util;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

public class ArsArmsProjectileData {
    private static Entity targetEntity;
    private static BlockHitResult blockHitResult;
    private static InteractionHand hand;
    private static boolean isEnabled = false;
    private static ItemStack iGun;

    public static void set(Entity target, BlockHitResult block, InteractionHand h, ItemStack gun) {
        targetEntity = target;
        blockHitResult = block;
        hand = h;
        isEnabled = true;
        iGun = gun;
    }

    public static Entity getTargetEntity() {
        return targetEntity;
    }

    public static BlockHitResult getBlockHitResult() {
        return blockHitResult;
    }

    public static InteractionHand getHand() {
        return hand;
    }

    public static boolean isEnabled() {
        return isEnabled;
    }

    public static ItemStack getCurrentGun() {
        return iGun;
    }

    public static void clear() {
        targetEntity = null;
        blockHitResult = null;
        hand = null;
        iGun = null;
        isEnabled = false;
    }
}

