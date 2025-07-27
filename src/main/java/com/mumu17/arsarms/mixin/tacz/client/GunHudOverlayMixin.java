package com.mumu17.arsarms.mixin.tacz.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mumu17.arsarms.util.ArsArmsAmmoUtil;
import com.tacz.guns.client.gui.overlay.GunHudOverlay;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GunHudOverlay.class)
public class GunHudOverlayMixin {

    @ModifyConstant(method = "handleInventoryAmmo", constant = @Constant(intValue = 0, ordinal = 0), remap = false)
    private static int handleInventoryAmmo(int constant, @Local(argsOnly = true)ItemStack stack, @Local(argsOnly = true) Inventory inventory) {
        return ArsArmsAmmoUtil.handleInventoryAmmo(stack, inventory);
    }

    @ModifyExpressionValue(method = "handleInventoryAmmo", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IAmmo;isAmmoOfGun(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"), remap = false)
    private static boolean isAmmoOfGun(boolean original) {
        return false;
    }

    @ModifyExpressionValue(method = "handleInventoryAmmo", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IAmmoBox;isAmmoBoxOfGun(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"), remap = false)
    private static boolean isAmmoBoxOfGun(boolean original) {
        return false;
    }
}
