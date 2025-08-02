package com.mumu17.arsarms.mixin.tacz.client;

import com.mumu17.arsarms.network.ArsArmsNetworkHandler;
import com.mumu17.arsarms.network.RequestSyncReloadArsModeMessage;
import com.mumu17.arsarms.util.ArsArmsAmmoUtil;
import com.mumu17.arsarms.util.GunItemNbt;
import com.mumu17.arscurios.util.ArsCuriosInventoryHelper;
import com.mumu17.arscurios.util.ArsCuriosLivingEntity;
import com.mumu17.arscurios.util.ExtendedHand;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.client.player.LocalPlayer;
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
    private final int MAX_AMMO_COUNT = 9999;

    @Inject(method = "tickStateLock", at = @At(value = "FIELD", target = "Lcom/tacz/guns/client/gameplay/LocalPlayerDataHolder;clientStateLock:Z"), remap = false)
    private void tickStateLock(CallbackInfo ci) {
        if (this.clientStateLock) {
            if (player != null) {
                ItemStack currentGunItem = player.getMainHandItem();
                if (currentGunItem.getItem() instanceof AbstractGunItem && currentGunItem.getItem() instanceof IGun iGun) {
                    GunItemNbt access = (GunItemNbt) iGun;
                    boolean reloadAmmoData = access.getIsArsMode(currentGunItem);
                    ExtendedHand hand = ArsCuriosLivingEntity.getPlayerExtendedHand(player);
                    boolean useInventoryAmmo = iGun.useInventoryAmmo(currentGunItem);
                    CommonGunIndex index = TimelessAPI.getCommonGunIndex(iGun.getGunId(currentGunItem)).orElse(null);
                    if (index != null) {
                        GunData gunData = index.getGunData();
                        if (gunData != null) {
                            int ammoCount = useInventoryAmmo ? ArsArmsAmmoUtil.handleInventoryAmmo(currentGunItem, player.getInventory()) + (iGun.hasBulletInBarrel(currentGunItem) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0) :
                                    iGun.getCurrentAmmoCount(currentGunItem) + (iGun.hasBulletInBarrel(currentGunItem) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0);
                            ammoCount = Math.min(ammoCount, MAX_AMMO_COUNT);
                            if (ammoCount <= 0 && reloadAmmoData) {
                                ArsArmsNetworkHandler.CHANNEL.sendToServer(new RequestSyncReloadArsModeMessage(player.getInventory().selected, hand.getSlotName(),3));
                            }
                        }
                    }
                }
            }
        }
    }
}
