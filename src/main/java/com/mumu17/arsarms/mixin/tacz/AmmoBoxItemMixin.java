package com.mumu17.arsarms.mixin.tacz;

import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.resource.index.CommonAmmoIndex;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AmmoBoxItem.class)
public class AmmoBoxItemMixin {

    @Inject(method = "lambda$overrideStackedOnOther$0", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/item/AmmoBoxItem;setAmmoCount(Lnet/minecraft/world/item/ItemStack;I)V"), remap = false)
    private void overrideStackedOnOther$0(int boxAmmoCount, ResourceLocation boxAmmoId, Slot slot, ItemStack ammoBox, Player player, CommonAmmoIndex index, CallbackInfo ci) {
        ammoBox.getOrCreateTag().putInt("LastAmmoStackSize", index.getStackSize());
    }

    @Inject(method = "lambda$overrideStackedOnOther$1", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/item/AmmoBoxItem;setAmmoCount(Lnet/minecraft/world/item/ItemStack;I)V"), remap = false)
    private void overrideStackedOnOther$1(ItemStack ammoBox, Slot slot, ItemStack slotItem, Player player, CommonAmmoIndex index, CallbackInfo ci) {
        ammoBox.getOrCreateTag().putInt("LastAmmoStackSize", index.getStackSize());
    }
}
