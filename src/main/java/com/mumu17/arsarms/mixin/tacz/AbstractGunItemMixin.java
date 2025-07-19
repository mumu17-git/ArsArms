package com.mumu17.arsarms.mixin.tacz;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mumu17.arsarms.util.ArsArmsAmmoBoxItemDataAccessor;
import com.mumu17.arsarms.util.ArsArmsAmmoItemDataAccessor;
import com.mumu17.arsarms.util.PlayerAmmoConsumer;
import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractGunItem.class)
public class AbstractGunItemMixin {

    @Inject(method = "findAndExtractInventoryAmmo", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/IItemHandler;getSlots()I", shift = At.Shift.BEFORE), cancellable = true, remap = false)
    public void findAndExtractInventoryAmmo(IItemHandler itemHandler, ItemStack gunItem, int needAmmoCount, CallbackInfoReturnable<Integer> cir, @Local(name = "cnt") int cnt) {
        ItemStack checkAmmoStack = PlayerAmmoConsumer.getOffHand();
        Item extractItem = checkAmmoStack.getItem();
        if (extractItem instanceof IAmmoBox iAmmoBox) {
            if (iAmmoBox.isAmmoBoxOfGun(gunItem, checkAmmoStack)) {
                int boxAmmoCount = iAmmoBox.getAmmoCount(checkAmmoStack);
                int extractCount = Math.min(boxAmmoCount, cnt);
                int remainCount = boxAmmoCount - extractCount;
                iAmmoBox.setAmmoCount(checkAmmoStack, remainCount);
                if (remainCount <= 0) {
                    iAmmoBox.setAmmoId(checkAmmoStack, DefaultAssets.EMPTY_AMMO_ID);
                }

                cnt -= extractCount;
                if (cnt <= 0) {
                    cir.setReturnValue(needAmmoCount - cnt);
                }
            }
        }
    }

    @ModifyExpressionValue(method = "findAndExtractInventoryAmmo", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IAmmo;isAmmoOfGun(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"), remap = false)
    public boolean findAndExtractInventoryAmmo(boolean original, @Local(name = "gunItem") ItemStack gunItem, @Local(name = "checkAmmoStack") ItemStack checkAmmoStack, @Local(name = "cnt")int cnt, @Local(name = "needAmmoCount")int needAmmoCount) {
        ArsArmsAmmoItemDataAccessor accessor = new ArsArmsAmmoItemDataAccessor();
        return accessor.isAmmoOfGun(gunItem, checkAmmoStack);
    }

    @ModifyExpressionValue(method = "findAndExtractInventoryAmmo", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IAmmoBox;isAmmoBoxOfGun(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"), remap = false)
    public boolean findAndExtractInventoryAmmo(boolean original, @Local(name = "iAmmoBox") IAmmoBox iAmmoBox, @Local(name = "gunItem") ItemStack gunItem, @Local(name = "checkAmmoStack") ItemStack checkAmmoStack, @Local(name = "cnt")int cnt, @Local(name = "needAmmoCount")int needAmmoCount) {
        ArsArmsAmmoBoxItemDataAccessor accessor = new ArsArmsAmmoBoxItemDataAccessor();
        return accessor.isAmmoBoxOfGun(gunItem, checkAmmoStack);
    }

    @ModifyExpressionValue(method = "lambda$hasInventoryAmmo$6", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IAmmo;isAmmoOfGun(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"), remap = false)
    private static boolean hasInventoryAmmo(boolean original, @Local(name = "gun") ItemStack gunItem, @Local(name = "checkAmmoStack") ItemStack checkAmmoStack) {
        ArsArmsAmmoItemDataAccessor accessor = new ArsArmsAmmoItemDataAccessor();
        return accessor.isAmmoOfGun(gunItem, checkAmmoStack);
    }

    @ModifyExpressionValue(method = "lambda$hasInventoryAmmo$6", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IAmmoBox;isAmmoBoxOfGun(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"), remap = false)
    private static boolean hasInventoryAmmoBox(boolean original, @Local(name = "gun") ItemStack gunItem, @Local(name = "checkAmmoStack") ItemStack checkAmmoStack) {
        ArsArmsAmmoBoxItemDataAccessor accessor = new ArsArmsAmmoBoxItemDataAccessor();
        return accessor.isAmmoBoxOfGun(gunItem, checkAmmoStack);
    }

}
