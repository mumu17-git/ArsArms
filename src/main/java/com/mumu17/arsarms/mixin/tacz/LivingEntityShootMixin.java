package com.mumu17.arsarms.mixin.tacz;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import com.mumu17.arsarms.util.*;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.ShootResult;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.entity.shooter.LivingEntityShoot;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.item.ModernKineticGunItem;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(LivingEntityShoot.class)
public class LivingEntityShootMixin {
    @Shadow(remap = false) @Final private LivingEntity shooter;
    @Shadow(remap = false) @Final private ShooterDataHolder data;

    @Unique
    private static final int MAX_AMMO_COUNT = 9999;

    @Inject(method = "shoot", at = @At(value = "RETURN"), remap = false)
    public void shoot(Supplier<Float> pitch, Supplier<Float> yaw, long timestamp, CallbackInfoReturnable<ShootResult> cir) {
        if (this.data.currentGunItem != null) {
            ItemStack currentGunItem = this.data.currentGunItem.get();
            if (currentGunItem.getItem() instanceof AbstractGunItem gunItem) {
                if (gunItem.useInventoryAmmo(currentGunItem)) {
                    if (shooter instanceof Player player) {
                        ItemStack offhand = player.getItemInHand(InteractionHand.OFF_HAND);
                        ArsArmsReloadArsModeActive.active(currentGunItem, offhand);
                    }
                }

                if (this.data.currentGunItem.get().getItem() instanceof ModernKineticGunItem modernKineticGunItem) {
                    GunItemCooldown gunItemCooldown = (GunItemCooldown) this.data.currentGunItem.get().getItem();
                    long nowTime = System.currentTimeMillis();
                    Player player = (Player) this.shooter;
                    CommonGunIndex index = TimelessAPI.getCommonGunIndex(modernKineticGunItem.getGunId(this.data.currentGunItem.get())).orElse(null);
                    if (index != null) {
                        GunData gunData = index.getGunData();
                        int ammoCount = gunItem.useInventoryAmmo(this.data.currentGunItem.get()) ? handleInventoryAmmo(this.data.currentGunItem.get(), player.getInventory()) + (modernKineticGunItem.hasBulletInBarrel(this.data.currentGunItem.get()) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0) :
                                modernKineticGunItem.getCurrentAmmoCount(this.data.currentGunItem.get()) + (modernKineticGunItem.hasBulletInBarrel(this.data.currentGunItem.get()) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0);
                        ammoCount = Math.min(ammoCount, MAX_AMMO_COUNT);
                        gunItemCooldown.setLastTimestamp(this.data.currentGunItem.get(), nowTime);
                        gunItemCooldown.setLastAmmoCount(this.data.currentGunItem.get(), ammoCount);
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


    @Inject(method = "consumeAmmoFromPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getCapability(Lnet/minecraftforge/common/capabilities/Capability;Lnet/minecraft/core/Direction;)Lnet/minecraftforge/common/util/LazyOptional;"), remap = false)
    public void consumeAmmoFromPlayer(CallbackInfo ci) {
        PlayerAmmoConsumer.setOffhand(shooter.getOffhandItem());
    }


}
