package com.mumu17.arsarms.mixin;

import com.mumu17.armslib.util.GunItemNbt;
import com.mumu17.arsarms.util.*;
import com.mumu17.arscurios.util.ArsCuriosInventoryHelper;
import com.mumu17.arscurios.util.ArsCuriosLivingEntity;
import com.mumu17.arscurios.util.ExtendedHand;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.resource.pojo.data.gun.BulletData;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Unique
    private static final int MAX_AMMO_COUNT = 9999;

    @Unique
    private static final long COOL_DOWN_TIME = 500L;

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Player player) {
            ArsArms$ArsModeCheck(player);
            ItemStack mainhand = player.getMainHandItem();
            if (mainhand.getItem() instanceof AbstractGunItem gunItem) {
                GunItemNbt access = (GunItemNbt) gunItem;
                if (!gunItem.useInventoryAmmo(mainhand)) {
                    long nowTime = System.currentTimeMillis();
                    long timestamp = access.getLastTimestamp(mainhand);
                    if (mainhand.getItem() instanceof IGun iGun) {
                        CommonGunIndex index = TimelessAPI.getCommonGunIndex(iGun.getGunId(mainhand)).orElse(null);
                        if (index != null) {
                            GunData gunData = index.getGunData();
                            int ammoCount = gunItem.useInventoryAmmo(mainhand) ? ArsArmsAmmoUtil.handleInventoryAmmo(mainhand, player.getInventory()) + (iGun.hasBulletInBarrel(mainhand) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0) :
                                    iGun.getCurrentAmmoCount(mainhand) + (iGun.hasBulletInBarrel(mainhand) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0);
                            ammoCount = Math.min(ammoCount, MAX_AMMO_COUNT);

                            ResourceLocation ammoId = gunData.getAmmoId();
                            Optional<CommonGunIndex> gunIndexOpt = TimelessAPI.getCommonGunIndex(ammoId);
                            int coolDownTimeModifier = ArsArms$getCoolDownTimeModifier(gunIndexOpt);

                            int lastAmmoCount = access.getLastAmmoCount(mainhand);

                            if (timestamp > 0) {
                                if (nowTime - timestamp > COOL_DOWN_TIME * coolDownTimeModifier && ammoCount <= lastAmmoCount && lastAmmoCount <= 0) {
                                    access.setLastAmmoCount(mainhand, -1);
                                    ArsArmsReloadArsModeCancel.remove(mainhand, player);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Unique
    private void ArsArms$ArsModeCheck(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (i == player.getInventory().selected) {
                if (stack.getItem() instanceof IGun iGun) {
                    GunItemNbt access = (GunItemNbt) iGun;
                    CommonGunIndex index = TimelessAPI.getCommonGunIndex(iGun.getGunId(stack)).orElse(null);
                    if (index != null) {
                        GunData gunData = index.getGunData();
                        if (gunData != null) {
                            int ammoCount = iGun.useInventoryAmmo(stack) ? ArsArmsAmmoUtil.handleInventoryAmmo(stack, player.getInventory()) + (iGun.hasBulletInBarrel(stack) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0) :
                                    iGun.getCurrentAmmoCount(stack) + (iGun.hasBulletInBarrel(stack) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0);
                            ammoCount = Math.min(ammoCount, MAX_AMMO_COUNT);
                            if (ammoCount <= 0 || access.getLastAmmoCount(stack) <= -1 || iGun.useInventoryAmmo(stack)) {
                                ExtendedHand originalHand = ArsCuriosLivingEntity.getPlayerExtendedHand(player);
                                ExtendedHand hand = ArsArmsCuriosUtil.getCuriosSlotFromGun(player, stack);
                                ItemStack curiosStack = ArsCuriosInventoryHelper.getCuriosInventoryItem(player, hand.getSlotName());
                                access.setOwner(stack, player);
                                if (!(curiosStack.getItem() instanceof AmmoBoxItem && hand.isAmmoBox())) {
                                    ArsCuriosLivingEntity.setPlayerExtendedHand(player, originalHand);
                                }
                                hand = ArsCuriosLivingEntity.getPlayerExtendedHand(player);
                                if (curiosStack.getItem() instanceof AmmoBoxItem && hand.isAmmoBox() && !access.getIsArsMode(stack)) {
                                    ArsArmsReloadArsModeActive.active(stack, curiosStack, false);
                                } else if (access.getIsArsMode(stack)) {
                                    ArsArmsReloadArsModeCancel.remove(stack, player);
                                }
                            }
                        }
                    }
                }
                continue;
            }
            if (stack.getItem() instanceof IGun iGun) {
                GunItemNbt access = (GunItemNbt) iGun;
                access.setOwner(stack, player);
                CommonGunIndex index = TimelessAPI.getCommonGunIndex(iGun.getGunId(stack)).orElse(null);
                if (index != null) {
                    GunData gunData = index.getGunData();
                    if (gunData != null) {
                        int ammoCount = iGun.useInventoryAmmo(stack) ? ArsArmsAmmoUtil.handleInventoryAmmo(stack, player.getInventory()) + (iGun.hasBulletInBarrel(stack) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0) :
                                iGun.getCurrentAmmoCount(stack) + (iGun.hasBulletInBarrel(stack) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0);
                        ammoCount = Math.min(ammoCount, MAX_AMMO_COUNT);
                        if ((iGun.useInventoryAmmo(stack) || (!iGun.useInventoryAmmo(stack) && ammoCount <= 0)) && access.getIsArsMode(stack)) {
                            ArsArmsReloadArsModeCancel.remove(stack, player);
                        }
                    }
                }
            }
        }
    }

    @Unique
    private static int ArsArms$getCoolDownTimeModifier(Optional<CommonGunIndex> gunIndexOpt) {
        boolean isExplosive = false;
        if (gunIndexOpt.isPresent()) {
            BulletData bulletData = gunIndexOpt.get().getBulletData();
            if (bulletData.getExplosionData() != null)
                isExplosive = true;
        }

        int coolDownTimeModifier = 1;
        if (isExplosive) {
            coolDownTimeModifier = 5;
        }
        return coolDownTimeModifier;
    }

}
