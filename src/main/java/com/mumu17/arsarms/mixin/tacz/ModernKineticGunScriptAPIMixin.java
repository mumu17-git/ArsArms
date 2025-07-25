package com.mumu17.arsarms.mixin.tacz;

import com.mumu17.arsarms.util.GunItemNbt;
import com.mumu17.arscurios.util.ArsCuriosInventoryHelper;
import com.mumu17.arscurios.util.ExtendedHand;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.item.AmmoItem;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModernKineticGunScriptAPI.class)
public abstract class ModernKineticGunScriptAPIMixin {

    @Shadow(remap = false) private ItemStack itemStack;

    @Redirect(method = "lambda$hasAmmoToConsume$5", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IAmmo;isAmmoOfGun(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"), remap = false)
    public boolean hasAmmoToConsumeAmmo(IAmmo instance, ItemStack gunItem, ItemStack checkAmmoStack) {
        AmmoItem ammoItem = (AmmoItem) instance;
        return ammoItem.isAmmoOfGun(gunItem, checkAmmoStack);
    }

    @Redirect(method = "lambda$hasAmmoToConsume$5", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IAmmoBox;isAmmoBoxOfGun(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"), remap = false)
    public boolean hasAmmoToConsumeAmmoBox(IAmmoBox instance, ItemStack gunItem, ItemStack checkAmmoStack) {
        AmmoBoxItem ammoBoxItem = (AmmoBoxItem) instance;
        return ammoBoxItem.isAmmoBoxOfGun(gunItem, checkAmmoStack);
    }

    @Inject(method = "lambda$hasAmmoToConsume$5", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/IItemHandler;getSlots()I"), cancellable = true, remap = false)
    public void hasAmmoToConsumeReturn(IItemHandler cap, CallbackInfoReturnable<Boolean> cir) {
        for (int i = 0; i < ExtendedHand.values().length; i++) {
            GunItemNbt access = (GunItemNbt) itemStack.getItem();
            LivingEntity owner = access.getOwner(itemStack);
            ItemStack checkAmmoStack = ArsCuriosInventoryHelper.getCuriosInventoryItem(owner, ExtendedHand.values()[i].getSlotName());
            if (checkAmmoStack.getItem() instanceof AmmoBoxItem iAmmoBox && iAmmoBox.isAmmoBoxOfGun(itemStack, checkAmmoStack)) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }
}
