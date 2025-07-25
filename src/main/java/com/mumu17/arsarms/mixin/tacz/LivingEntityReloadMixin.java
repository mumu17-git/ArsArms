package com.mumu17.arsarms.mixin.tacz;

import com.llamalad7.mixinextras.sugar.Local;
import com.mumu17.arsarms.ArsArms;
import com.mumu17.arscurios.util.ArsCuriosInventoryHelper;
import com.mumu17.arscurios.util.ArsCuriosLivingEntity;
import com.mumu17.arscurios.util.ExtendedHand;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.entity.shooter.LivingEntityReload;
import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.resource.index.CommonGunIndex;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityReload.class)
public class LivingEntityReloadMixin {

    @Shadow(remap = false)
    @Final private LivingEntity shooter;

    @ModifyVariable(method = "lambda$reload$0", at = @At(value = "STORE"), remap = false)
    private int onReload_ammoCount(int value) {

        return value;
    }

    @Inject(method = "lambda$reload$0", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/gun/AbstractGunItem;startReload(Lcom/tacz/guns/entity/shooter/ShooterDataHolder;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Z"), remap = false)
    private void onReload(AbstractGunItem gunItem, ItemStack currentGunItem, CommonGunIndex gunIndex, CallbackInfo ci) {
        if (shooter instanceof Player) {
            for (ExtendedHand hand : ExtendedHand.values()) {
                ItemStack curiosItemStack = ArsCuriosInventoryHelper.getCuriosInventoryItem(shooter, hand.getSlotName());
                if (curiosItemStack.getItem() instanceof AmmoBoxItem iAmmoBox) {
                    if (iAmmoBox.isAmmoBoxOfGun(currentGunItem, curiosItemStack)) {
                        ArsCuriosLivingEntity.setPlayerExtendedHand(shooter, hand);
                        break;
                    }
                }
            }
        }
    }

}
