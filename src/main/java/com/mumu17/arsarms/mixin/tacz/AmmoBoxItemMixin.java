package com.mumu17.arsarms.mixin.tacz;

import com.mumu17.arsarms.util.ArsArmsAmmoUtil;
import com.mumu17.arsarms.util.GunItemNbt;
import com.mumu17.arscurios.util.ArsCuriosInventoryHelper;
import com.mumu17.arscurios.util.ArsCuriosLivingEntity;
import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.nbt.AmmoBoxItemDataAccessor;
import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.resource.index.CommonAmmoIndex;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AmmoBoxItem.class)
@Implements(value = @Interface(iface = AmmoBoxItemDataAccessor.class, prefix = "AmmoBoxItemDataAccessor$"))
public class AmmoBoxItemMixin {

    @Inject(method = "lambda$overrideStackedOnOther$0", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/item/AmmoBoxItem;setAmmoCount(Lnet/minecraft/world/item/ItemStack;I)V"), remap = false)
    private void overrideStackedOnOther$0(int boxAmmoCount, ResourceLocation boxAmmoId, Slot slot, ItemStack ammoBox, Player player, CommonAmmoIndex index, CallbackInfo ci) {
        ammoBox.getOrCreateTag().putInt("LastAmmoStackSize", index.getStackSize());
    }

    @Inject(method = "lambda$overrideStackedOnOther$1", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/item/AmmoBoxItem;setAmmoCount(Lnet/minecraft/world/item/ItemStack;I)V"), remap = false)
    private void overrideStackedOnOther$1(ItemStack ammoBox, Slot slot, ItemStack slotItem, Player player, CommonAmmoIndex index, CallbackInfo ci) {
        ammoBox.getOrCreateTag().putInt("LastAmmoStackSize", index.getStackSize());
    }

    public boolean AmmoBoxItemDataAccessor$isAmmoBoxOfGun(ItemStack gun, ItemStack ammoBox) {
        Item var5 = gun.getItem();
        if (var5 instanceof IGun iGun) {
            var5 = ammoBox.getItem();
            if (var5 instanceof IAmmoBox iAmmoBox) {
                if (((GunItemNbt) iGun).getIsArsMode(gun) != ArsArmsAmmoUtil.isAmmoBoxArsMode(ammoBox)) {
                    return false;
                }

                if (((AmmoBoxItem)(Object)this).isAllTypeCreative(ammoBox)) {
                    return true;
                }

                ResourceLocation ammoId = iAmmoBox.getAmmoId(ammoBox);
                if (ammoId.equals(DefaultAssets.EMPTY_AMMO_ID)) {
                    return false;
                }


                GunItemNbt access = (GunItemNbt) iGun;
                LivingEntity owner = access.getOwner(gun);
                ItemStack stack = ArsCuriosInventoryHelper.getCuriosInventoryItem(owner, ArsCuriosLivingEntity.getPlayerExtendedHand(owner).getSlotName());
                if (stack.getItem() instanceof IAmmoBox stackIAmmoBox){
                    ResourceLocation stackAmmoId = stackIAmmoBox.getAmmoId(stack);
                    if (!ammoId.equals(stackAmmoId)) {
                        return false;
                    }
                }
                CommonGunIndex index = TimelessAPI.getCommonGunIndex(iGun.getGunId(gun)).orElse(null);
                if (index != null) {
                    GunData gunData = index.getGunData();
                    ResourceLocation gunAmmoId = gunData.getAmmoId();
                    if (!ammoId.equals(gunAmmoId)) {
                        return false;
                    }
                }

                ResourceLocation gunId = iGun.getGunId(gun);
                return (Boolean) TimelessAPI.getCommonGunIndex(gunId).map((gunIndex) -> gunIndex.getGunData().getAmmoId().equals(ammoId)).orElse(false);
            }
        }

        return false;
    }
}
