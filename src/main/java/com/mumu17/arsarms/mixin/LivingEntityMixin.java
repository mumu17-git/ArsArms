package com.mumu17.arsarms.mixin;

import com.mumu17.arsarms.util.*;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.item.ModernKineticGunItem;
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
            ItemStack mainhand = player.getMainHandItem();
            // PlayerAmmoConsumer.setPlayer(player);
            PlayerAmmoConsumer.setOffhand(player.getOffhandItem());
            if (mainhand.getItem() instanceof AbstractGunItem gunItem) {
                if (gunItem.useInventoryAmmo(mainhand)) {
                    GunItemCooldown gunItemCooldown = (GunItemCooldown) gunItem;
                    long nowTime = System.currentTimeMillis();
                    long timestamp = gunItemCooldown.getLastTimestamp(mainhand);
                    if (timestamp > 0) {
                        if (nowTime - timestamp > COOL_DOWN_TIME);
                            // ArsArmsReloadArsModeCancel.removeReactiveFromGun(mainhand, player);
                    }
                } else {
                    GunItemCooldown gunItemCooldown = (GunItemCooldown) gunItem;
                    long nowTime = System.currentTimeMillis();
                    long timestamp = gunItemCooldown.getLastTimestamp(mainhand);
                    if (mainhand.getItem() instanceof ModernKineticGunItem modernKineticGunItem) {
                        CommonGunIndex index = TimelessAPI.getCommonGunIndex(modernKineticGunItem.getGunId(mainhand)).orElse(null);
                        if (index != null) {
                            GunData gunData = index.getGunData();
                            int ammoCount = gunItem.useInventoryAmmo(mainhand) ? ArsArmsAmmoUtil.handleInventoryAmmo(mainhand, player.getInventory()) + (modernKineticGunItem.hasBulletInBarrel(mainhand) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0) :
                                    modernKineticGunItem.getCurrentAmmoCount(mainhand) + (modernKineticGunItem.hasBulletInBarrel(mainhand) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0);
                            ammoCount = Math.min(ammoCount, MAX_AMMO_COUNT);

                            ResourceLocation ammoId = gunData.getAmmoId();
                            Optional<CommonGunIndex> gunIndexOpt = TimelessAPI.getCommonGunIndex(ammoId);
                            int coolDownTimeModifier = ArsArms$getCoolDownTimeModifier(gunIndexOpt);

                            int lastAmmoCount = gunItemCooldown.getLastAmmoCount(mainhand);
                            if (timestamp > 0) {
                                if (nowTime - timestamp > COOL_DOWN_TIME * coolDownTimeModifier && ammoCount <= lastAmmoCount && lastAmmoCount <= 0) {
                                    gunItemCooldown.setLastAmmoCount(mainhand, -1);
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
