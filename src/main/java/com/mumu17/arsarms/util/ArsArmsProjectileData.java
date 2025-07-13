package com.mumu17.arsarms.util;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class ArsArmsProjectileData {
    private static Entity targetEntity;
    private static BlockHitResult blockHitResult;
    private static InteractionHand hand;
    private static boolean isEnabled = false;
    private static float damageMultiplier;

    public static void set(Entity target, BlockHitResult block, InteractionHand h, float damageMulti) {
        targetEntity = target;
        blockHitResult = block;
        hand = h;
        isEnabled = true;
        damageMultiplier = damageMulti;
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

    public static float getDamageMultiplier() {
        return damageMultiplier;
    }

    public static void clear() {
        targetEntity = null;
        blockHitResult = null;
        hand = null;
        isEnabled = false;
    }
}

