package com.mumu17.arsarms.mixin.tacz.client;

import com.mumu17.arsarms.util.*;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;
import com.tacz.guns.item.ModernKineticGunItem;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayerDataHolder.class)
public class LocalPlayerDataHolderMixin {

    @Shadow(remap = false)
    public volatile boolean clientStateLock;
    @Final
    @Shadow(remap = false)
    private LocalPlayer player;

    @Unique
    private ItemStack currentGunItem = ItemStack.EMPTY;

    @Unique
    private ItemStack offhand = ItemStack.EMPTY;

    @Unique
    private final int MAX_AMMO_COUNT = 9999;

    @Inject(method = "tickStateLock", at = @At(value = "FIELD", target = "Lcom/tacz/guns/client/gameplay/LocalPlayerDataHolder;clientStateLock:Z"), remap = false)
    private void tickStateLock(CallbackInfo ci) {
        if (this.clientStateLock) {
            if (player != null) {
                currentGunItem = player.getMainHandItem();
                offhand = player.getOffhandItem();
                if (currentGunItem.getItem() instanceof AbstractGunItem gi && currentGunItem.getItem() instanceof ModernKineticGunItem modernKineticGunItem) {
                    ModernKineticGunItemAccess access = (ModernKineticGunItemAccess) modernKineticGunItem;
                    ArsArmsReloadAmmoData reloadAmmoData = access.getReloadAmoData(currentGunItem);
                    boolean useInventoryAmmo = modernKineticGunItem.useInventoryAmmo(currentGunItem);
                    CommonGunIndex index = TimelessAPI.getCommonGunIndex(modernKineticGunItem.getGunId(currentGunItem)).orElse(null);
                    if (index != null) {
                        GunData gunData = index.getGunData();
                        if (gunData != null) {
                            if (currentGunItem.getItem() instanceof IGun iGun) {
                                int ammoCount = useInventoryAmmo ? ArsArmsAmmoUtil.handleInventoryAmmo(currentGunItem, player.getInventory()) + (iGun.hasBulletInBarrel(currentGunItem) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0) :
                                        iGun.getCurrentAmmoCount(currentGunItem) + (iGun.hasBulletInBarrel(currentGunItem) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0);
                                ammoCount = Math.min(ammoCount, MAX_AMMO_COUNT);
                                if (ammoCount <= 0 && reloadAmmoData != null && reloadAmmoData.isArsMode()) {
                                    ArsArmsReloadArsModeCancel.cancel(currentGunItem, offhand);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
