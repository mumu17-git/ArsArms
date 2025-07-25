package com.mumu17.arsarms.mixin.tacz.client;

import com.mumu17.arsarms.util.GunItemNbt;
import com.mumu17.arscurios.util.ArsCuriosInventoryHelper;
import com.mumu17.arscurios.util.ExtendedHand;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.client.animation.statemachine.GunAnimationStateContext;
import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.item.AmmoItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GunAnimationStateContext.class)
public class GunAnimationStateContextMixin {

    @Shadow(remap = false)
    private ItemStack currentGunItem;

    @Redirect(method = "lambda$hasAmmoToConsume$7", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IAmmo;isAmmoOfGun(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"), remap = false)
    private boolean hasAmmoToConsume_isAmmoOfGun(IAmmo instance, ItemStack gunItem, ItemStack checkAmmoStack) {
        AmmoItem ammoItem = (AmmoItem) instance;
        return ammoItem.isAmmoOfGun(gunItem, checkAmmoStack);
    }

    @Redirect(method = "lambda$hasAmmoToConsume$7", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IAmmoBox;isAmmoBoxOfGun(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"), remap = false)
    private boolean hasAmmoToConsume_isAmmoBoxOfGun(IAmmoBox instance, ItemStack gunItem, ItemStack checkAmmoStack) {
        AmmoBoxItem ammoBoxItem = (AmmoBoxItem) instance;
        return ammoBoxItem.isAmmoBoxOfGun(gunItem, checkAmmoStack);
    }

    @Inject(method = "lambda$hasAmmoToConsume$7", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/IItemHandler;getSlots()I"), cancellable = true, remap = false)
    private void hasAmmoToConsume(IItemHandler cap, CallbackInfoReturnable<Boolean> cir) {
        for (int i = 0; i < ExtendedHand.values().length; i++) {
            GunItemNbt access = (GunItemNbt) currentGunItem.getItem();
            LivingEntity owner = access.getOwner(currentGunItem);
            ItemStack checkAmmoStack = ArsCuriosInventoryHelper.getCuriosInventoryItem(owner, ExtendedHand.values()[i].getSlotName());
            if (checkAmmoStack.getItem() instanceof AmmoBoxItem iAmmoBox && iAmmoBox.isAmmoBoxOfGun(currentGunItem, checkAmmoStack)) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }
}
