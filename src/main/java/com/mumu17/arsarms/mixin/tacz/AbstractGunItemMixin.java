package com.mumu17.arsarms.mixin.tacz;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.mumu17.arsarms.util.GunItemNbt;
import com.mumu17.arscurios.util.ArsCuriosInventoryHelper;
import com.mumu17.arscurios.util.ExtendedHand;
import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.item.AmmoItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractGunItem.class)
public class AbstractGunItemMixin {

    @Inject(method = "findAndExtractInventoryAmmo", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/IItemHandler;getSlots()I"), remap = false, cancellable = true)
    public void findAndExtractInventoryAmmo(IItemHandler itemHandler, ItemStack gunItem, int needAmmoCount, CallbackInfoReturnable<Integer> cir, @Local(name = "cnt") LocalIntRef cnt) {
        ExtendedHand[] extendedHands = ExtendedHand.values();
        if (gunItem.getItem() instanceof IGun iGun) {
            for (ExtendedHand extendedHand: extendedHands) {
                GunItemNbt access = (GunItemNbt) iGun;
                LivingEntity owner = access.getOwner(gunItem);
                if (owner != null) {
                    ItemStack checkAmmoStack = (extendedHand.isCurios() ? ArsCuriosInventoryHelper.getCuriosInventoryItem(owner, extendedHand.getSlotName()) : owner.getItemInHand(InteractionHand.valueOf(extendedHand.getSlotName())));
                    Item extractItem = checkAmmoStack.getItem();
                    if (extractItem instanceof AmmoBoxItem iAmmoBox) {
                        if (iAmmoBox.isAmmoBoxOfGun(gunItem, checkAmmoStack)) {
                            int boxAmmoCount = iAmmoBox.getAmmoCount(checkAmmoStack);
                            int extractCount = Math.min(boxAmmoCount, cnt.get());
                            int remainCount = boxAmmoCount - extractCount;
                            iAmmoBox.setAmmoCount(checkAmmoStack, remainCount);
                            if (remainCount <= 0) {
                                iAmmoBox.setAmmoId(checkAmmoStack, DefaultAssets.EMPTY_AMMO_ID);
                            }
                            cnt.set(cnt.get() - extractCount);;
                            if (cnt.get() <= 0) {
                                cir.setReturnValue(needAmmoCount - cnt.get());
                                cir.cancel();
                            }
                        }
                    }
                }
            }
        }
    }

    @Redirect(method = "findAndExtractInventoryAmmo", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IAmmo;isAmmoOfGun(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"), remap = false)
    public boolean findAndExtractInventoryAmmo(IAmmo instance, ItemStack gunItem, ItemStack checkAmmoStack) {
        AmmoItem ammoItem = (AmmoItem) instance;
        return ammoItem.isAmmoOfGun(gunItem, checkAmmoStack);
    }

    @Redirect(method = "findAndExtractInventoryAmmo", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IAmmoBox;isAmmoBoxOfGun(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"), remap = false)
    public boolean findAndExtractInventoryAmmoBox(IAmmoBox instance, ItemStack gunItem, ItemStack checkAmmoStack) {
        AmmoBoxItem ammoBoxItem = (AmmoBoxItem) instance;
        return ammoBoxItem.isAmmoBoxOfGun(gunItem, checkAmmoStack);
    }

    @Redirect(method = "lambda$hasInventoryAmmo$6", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IAmmo;isAmmoOfGun(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"), remap = false)
    private static boolean hasInventoryAmmo(IAmmo instance, ItemStack gunItem, ItemStack checkAmmoStack) {
        AmmoItem ammoItem = (AmmoItem) instance;
        return ammoItem.isAmmoOfGun(gunItem, checkAmmoStack);
    }

    @Redirect(method = "lambda$hasInventoryAmmo$6", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IAmmoBox;isAmmoBoxOfGun(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"), remap = false)
    private static boolean hasInventoryAmmoBox(IAmmoBox instance, ItemStack gunItem, ItemStack checkAmmoStack) {
        AmmoBoxItem ammoBoxItem = (AmmoBoxItem) instance;
        return ammoBoxItem.isAmmoBoxOfGun(gunItem, checkAmmoStack);
    }

    @Inject(method = "lambda$hasInventoryAmmo$6", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/IItemHandler;getSlots()I"), cancellable = true, remap = false)
    private static void hasInventoryAmmoBox(ItemStack gun, IItemHandler cap, CallbackInfoReturnable<Boolean> cir) {
        for (int i = 0; i < ExtendedHand.values().length; i++) {
            GunItemNbt access = (GunItemNbt) gun.getItem();
            LivingEntity owner = access.getOwner(gun);
            ItemStack checkAmmoStack = ArsCuriosInventoryHelper.getCuriosInventoryItem(owner, ExtendedHand.values()[i].getSlotName());
            if (checkAmmoStack.getItem() instanceof AmmoBoxItem iAmmoBox && iAmmoBox.isAmmoBoxOfGun(gun, checkAmmoStack)) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }

    @Redirect(method = "lambda$canReload$1", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IAmmo;isAmmoOfGun(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"), remap = false)
    private static boolean canReload_isAmmoOfGun(IAmmo instance, ItemStack gunItem, ItemStack checkAmmoStack) {
        AmmoItem ammoItem = (AmmoItem) checkAmmoStack.getItem();
        return ammoItem.isAmmoOfGun(gunItem, checkAmmoStack);
    }

    @Redirect(method = "lambda$canReload$1", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IAmmoBox;isAmmoBoxOfGun(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"), remap = false)
    private static boolean canReload_isAmmoBoxOfGun(IAmmoBox instance, ItemStack gunItem, ItemStack checkAmmoStack) {
        AmmoBoxItem ammoBoxItem = (AmmoBoxItem) checkAmmoStack.getItem();
        return ammoBoxItem.isAmmoBoxOfGun(gunItem, checkAmmoStack);
    }

    @Inject(method = "lambda$canReload$1", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/IItemHandler;getSlots()I"), cancellable = true, remap = false)
    private static void canReload(ItemStack gun, IItemHandler cap, CallbackInfoReturnable<Boolean> cir) {
        for (int i = 0; i < ExtendedHand.values().length; i++) {
            GunItemNbt access = (GunItemNbt) gun.getItem();
            LivingEntity owner = access.getOwner(gun);
            ItemStack checkAmmoStack = ArsCuriosInventoryHelper.getCuriosInventoryItem(owner, ExtendedHand.values()[i].getSlotName());
            if (checkAmmoStack.getItem() instanceof AmmoBoxItem iAmmoBox && iAmmoBox.isAmmoBoxOfGun(gun, checkAmmoStack)) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }
}
