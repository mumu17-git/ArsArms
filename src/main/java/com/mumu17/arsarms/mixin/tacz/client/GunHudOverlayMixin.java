package com.mumu17.arsarms.mixin.tacz.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mumu17.arsarms.util.ArsArmsAmmoBoxItemDataAccessor;
import com.mumu17.arsarms.util.ArsArmsAmmoItemDataAccessor;
import com.tacz.guns.client.gui.overlay.GunHudOverlay;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GunHudOverlay.class)
public class GunHudOverlayMixin {

    @ModifyExpressionValue(method = "handleInventoryAmmo", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IAmmo;isAmmoOfGun(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"), remap = false)
    private static boolean handleInventoryAmmo(boolean original, @Local(name = "stack") ItemStack gunItem, @Local(name = "inventoryItem") ItemStack checkAmmoStack) {
        ArsArmsAmmoItemDataAccessor accessor = new ArsArmsAmmoItemDataAccessor();
        return accessor.isAmmoOfGun(gunItem, checkAmmoStack);
    }

    @ModifyExpressionValue(method = "handleInventoryAmmo", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IAmmoBox;isAmmoBoxOfGun(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"), remap = false)
    private static boolean handleInventoryAmmoBox(boolean original, @Local(name = "stack") ItemStack gunItem, @Local(name = "inventoryItem") ItemStack checkAmmoStack) {
        ArsArmsAmmoBoxItemDataAccessor accessor = new ArsArmsAmmoBoxItemDataAccessor();
        return accessor.isAmmoBoxOfGun(gunItem, checkAmmoStack);
    }
}
