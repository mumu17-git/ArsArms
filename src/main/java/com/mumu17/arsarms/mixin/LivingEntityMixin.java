package com.mumu17.arsarms.mixin;

import com.mumu17.arsarms.util.ArsArmsReloadAmmoData;
import com.mumu17.arsarms.util.GunItemCooldown;
import com.mumu17.arsarms.util.ModernKineticGunItemAccess;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.item.ModernKineticGunItem;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Unique
    private static final int MAX_AMMO_COUNT = 9999;

    @Unique
    private final long COOL_DOWN_TIME = 500L;

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Player player) {
            ItemStack mainhand = player.getMainHandItem();
            if (mainhand.getItem() instanceof AbstractGunItem gunItem) {
                if (gunItem.useInventoryAmmo(mainhand)) {
                    GunItemCooldown gunItemCooldown = (GunItemCooldown) gunItem;
                    long nowTime = System.currentTimeMillis();
                    long timestamp = gunItemCooldown.getLastTimestamp(mainhand);
                    if (timestamp > 0) {
                        if (nowTime - timestamp > COOL_DOWN_TIME)
                            removeReactiveFromGun(mainhand);
                    }
                }
            }
        }
    }

    @Unique
    private void removeReactiveFromGun(ItemStack gunItem) {
        if (gunItem != ItemStack.EMPTY && gunItem.getItem() instanceof ModernKineticGunItem modernKineticGunItem) {
            boolean useInventoryAmmo = modernKineticGunItem.useInventoryAmmo(gunItem);
            LivingEntity entity = (LivingEntity) (Object) this;
            if(entity instanceof Player player) {
                CommonGunIndex index = TimelessAPI.getCommonGunIndex(modernKineticGunItem.getGunId(gunItem)).orElse(null);
                if (index != null) {
                    GunData gunData = index.getGunData();
                    if (gunData != null) {
                        if (gunItem.getItem() instanceof IGun iGun) {
                            int ammoCount = useInventoryAmmo ? handleInventoryAmmo(gunItem, player.getInventory()) + (iGun.hasBulletInBarrel(gunItem) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0) :
                                    iGun.getCurrentAmmoCount(gunItem) + (iGun.hasBulletInBarrel(gunItem) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0);
                            ammoCount = Math.min(ammoCount, MAX_AMMO_COUNT);
                            ModernKineticGunItemAccess access = (ModernKineticGunItemAccess) iGun;
                            ArsArmsReloadAmmoData reloadAmmoData = access.getReloadAmoData(gunItem);
                            if (reloadAmmoData != null) {
                                boolean isArsMode = reloadAmmoData.isArsMode();
                                if (isArsMode) {
                                    if (gunItem.hasTag() && gunItem.getOrCreateTag().contains("ars_nouveau:reactive_caster")) {
                                        if (gunItem.getOrCreateTag().contains("Enchantments")) {
                                            ListTag enchantments = gunItem.getOrCreateTag().getList("Enchantments", Tag.TAG_COMPOUND);
                                            if (!enchantments.isEmpty()) {
                                                for (int i = 0; i < enchantments.size(); i++) {
                                                    CompoundTag enchantmentTag = enchantments.getCompound(i);
                                                    String enchantmentId = enchantmentTag.getString("id");
                                                    if ("ars_nouveau:reactive".equals(enchantmentId)) {
                                                        enchantments.remove(i);
                                                        gunItem.getOrCreateTag().put("Enchantments", enchantments);
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                        gunItem.getOrCreateTag().remove("ars_nouveau:reactive_caster");
                                    }
                                    access.setReloadAmoData(gunItem, false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @Unique
    private int handleInventoryAmmo(ItemStack stack, Inventory inventory) {
        int cacheInventoryAmmoCount = 0;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack inventoryItem = inventory.getItem(i);
            if (inventoryItem.getItem() instanceof IAmmo iAmmo && iAmmo.isAmmoOfGun(stack, inventoryItem)) {
                cacheInventoryAmmoCount += inventoryItem.getCount();
            }
            if (inventoryItem.getItem() instanceof IAmmoBox iAmmoBox && iAmmoBox.isAmmoBoxOfGun(stack, inventoryItem)) {
                if (iAmmoBox.isAllTypeCreative(inventoryItem) || iAmmoBox.isCreative(inventoryItem)) {
                    cacheInventoryAmmoCount = 9999;
                    return cacheInventoryAmmoCount;
                }
                cacheInventoryAmmoCount += iAmmoBox.getAmmoCount(inventoryItem);
            }
        }
        return cacheInventoryAmmoCount;
    }

}
