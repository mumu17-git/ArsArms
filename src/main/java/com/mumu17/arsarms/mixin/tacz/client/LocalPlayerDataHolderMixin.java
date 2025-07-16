package com.mumu17.arsarms.mixin.tacz.client;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import com.llamalad7.mixinextras.sugar.Local;
import com.mumu17.arsarms.util.*;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.ReloadState;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;
import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.item.ModernKineticGunItem;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.util.AttachmentDataUtils;
import jdk.jfr.Name;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
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

@Mixin(LocalPlayerDataHolder.class)
public class LocalPlayerDataHolderMixin {

    @Shadow(remap = false)
    public volatile boolean clientStateLock;

    @Unique
    private ItemStack currentGunItem = ItemStack.EMPTY;

    @Unique
    private ItemStack offhand = ItemStack.EMPTY;

    @Inject(method = "tickStateLock", at = @At(value = "FIELD", target = "Lcom/tacz/guns/client/gameplay/LocalPlayerDataHolder;clientStateLock:Z"), remap = false)
    private void tickStateLock(CallbackInfo ci) {
        if (this.clientStateLock) {
            Player player = PlayerAmmoConsumer.getPlayer();
            if (player != null) {
                currentGunItem = player.getMainHandItem();
                offhand = player.getOffhandItem();
                if (currentGunItem.getItem() instanceof AbstractGunItem gi && currentGunItem.getItem() instanceof ModernKineticGunItem modernKineticGunItem) {
                    ModernKineticGunItemAccess access = (ModernKineticGunItemAccess) modernKineticGunItem;
                    ArsArmsReloadAmmoData reloadAmmoData = access.getReloadAmoData(currentGunItem);
                    System.out.println(modernKineticGunItem.getCurrentAmmoCount(currentGunItem)+", "+reloadAmmoData);
                    if (modernKineticGunItem.getCurrentAmmoCount(currentGunItem) <= 0 && reloadAmmoData != null && reloadAmmoData.isArsMode()) {
                        ArsArmsReloadArsModeCancel.cancel(currentGunItem, offhand);
                    }
                }
            }
        }
    }
}
