package com.mumu17.arsarms.mixin.tacz;

import com.mumu17.armslib.util.ArmsLibAmmoUtil;
import com.mumu17.armslib.util.GunItemNbt;
import com.mumu17.arsarms.util.ArsArmsReloadArsModeActive;
import com.mumu17.arscurios.util.ArsCuriosInventoryHelper;
import com.mumu17.arscurios.util.ArsCuriosLivingEntity;
import com.mumu17.arscurios.util.ExtendedHand;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.ShootResult;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.entity.shooter.LivingEntityShoot;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
                    ExtendedHand hand = ArsCuriosLivingEntity.getPlayerExtendedHand(shooter);
                    ArsArmsReloadArsModeActive.active(currentGunItem, ArsCuriosInventoryHelper.getCuriosInventoryItem(shooter, hand.getSlotName()), shooter, true);
                }

                if (this.data.currentGunItem.get().getItem() instanceof IGun iGun) {
                    GunItemNbt access = (GunItemNbt) this.data.currentGunItem.get().getItem();
                    long nowTime = System.currentTimeMillis();
                    if (shooter instanceof Player player) {
                        CommonGunIndex index = TimelessAPI.getCommonGunIndex(iGun.getGunId(this.data.currentGunItem.get())).orElse(null);
                        if (index != null) {
                            GunData gunData = index.getGunData();
                            int ammoCount = gunItem.useInventoryAmmo(currentGunItem) ? ArmsLibAmmoUtil.handleInventoryAmmo(this.data.currentGunItem.get(), player.getInventory()) + (iGun.hasBulletInBarrel(this.data.currentGunItem.get()) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0) :
                                    iGun.getCurrentAmmoCount(this.data.currentGunItem.get()) + (iGun.hasBulletInBarrel(this.data.currentGunItem.get()) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0);
                            ammoCount = Math.min(ammoCount, MAX_AMMO_COUNT);
                            access.setLastTimestamp(this.data.currentGunItem.get(), nowTime);
                            access.setLastAmmoCount(this.data.currentGunItem.get(), ammoCount);
                        }
                    }
                }
            }
        }
    }
}
