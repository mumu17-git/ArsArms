package com.mumu17.arsarms.mixin.tacz;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mumu17.arsarms.util.*;
import com.mumu17.arscurios.util.ArsCuriosInventoryHelper;
import com.mumu17.arscurios.util.ExtendedHand;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.entity.shooter.LivingEntityReload;
import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.resource.index.CommonGunIndex;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityReload.class)
public class LivingEntityReloadMixin {

    @Shadow(remap = false)
    @Final private LivingEntity shooter;

    @ModifyExpressionValue(method = "lambda$reload$0", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/gun/AbstractGunItem;startReload(Lcom/tacz/guns/entity/shooter/ShooterDataHolder;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Z"), remap = false)
    private boolean onReload(boolean original, @Local(name = "gunItem") AbstractGunItem gunItem, @Local(name = "currentGunItem") ItemStack currentGunItem, @Local(name = "gunIndex") CommonGunIndex gunIndex) {
        if (shooter instanceof Player player) {
            for (ExtendedHand hand : ExtendedHand.values()) {
                ItemStack curiosItemStack = ArsCuriosInventoryHelper.getCuriosInventoryItem(shooter, hand.getSlotName());
                if (curiosItemStack.getItem() instanceof AmmoBoxItem) {
                    GunItemNbt access = (GunItemNbt) gunItem;
                    access.setReloadedSlot(currentGunItem, hand.getSlotName());
                    ArsArmsReloadArsModeActive.active(currentGunItem, curiosItemStack, true);
                }
            }
        }
        return original;
    }

}
