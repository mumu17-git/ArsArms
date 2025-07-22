package com.mumu17.arsarms.client;

import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.mumu17.arsarms.ArsArms;
import com.mumu17.arsarms.network.ArsArmsNetworkHandler;
import com.mumu17.arsarms.network.RequestSyncChargedManaMessage;
import com.mumu17.arsarms.util.*;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.item.ModernKineticGunItem;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsArms.MODID, value = Dist.CLIENT)
public class ChargeManaToAmmoBoxTick {
    private static final int MAX_AMMO_COUNT = 9999;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.ClientTickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                // PlayerAmmoConsumer.setPlayer(mc.player);
                ItemStack offhand = mc.player.getOffhandItem();
                if (isTargetItem(offhand)) {
                    chargeManaOrCancel(mc.player);
                }

                for (int i = 0; i < mc.player.getInventory().getContainerSize(); i++) {
                    ItemStack stack = mc.player.getInventory().getItem(i);
                    if (i == mc.player.getInventory().selected) {
                        if (offhand.getItem() instanceof AmmoBoxItem) {
                            if (stack.getItem() instanceof ModernKineticGunItem gunItem) {
                                if (gunItem.useInventoryAmmo(stack)) {
                                    CommonGunIndex index = TimelessAPI.getCommonGunIndex(gunItem.getGunId(stack)).orElse(null);
                                    if (index != null) {
                                        GunData gunData = index.getGunData();
                                        if (gunData != null) {
                                            int ammoCount = gunItem.useInventoryAmmo(stack) ? ArsArmsAmmoUtil.handleInventoryAmmo(stack, mc.player.getInventory()) + (gunItem.hasBulletInBarrel(stack) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0) :
                                                    gunItem.getCurrentAmmoCount(stack) + (gunItem.hasBulletInBarrel(stack) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0);
                                            ammoCount = Math.min(ammoCount, MAX_AMMO_COUNT);
                                            ArsArmsReloadArsModeActive.active(stack, offhand, false);
                                            continue;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (stack.getItem() instanceof ModernKineticGunItem gunItem) {
                        if (gunItem.useInventoryAmmo(stack)) {
                            ArsArmsReloadArsModeCancel.remove(stack, mc.player);
                        }
                    }
                }
            }
        }
    }

    private static void chargeManaOrCancel(LocalPlayer player) {
        ItemStack offhand = player.getOffhandItem();
        if (isTargetItem(offhand)) {
            int chargedManaCount = ArsArmsAmmoBox.getChargedManaCount(offhand);
            if (chargedManaCount < 0) {
                chargedManaCount = 0;
            }

            double mana = ManaUtil.getCurrentMana(player);
            if (mana < 100) {
                return;
            }

            int maxManaCount = ArsArmsAmmoBox.getMaxManaCount(offhand);

            int maxChargedManaCount = ArsArmsAmmoBox.getMaxChargedManaCount(offhand);

            if (chargedManaCount >= maxChargedManaCount) {
                return;
            }
            
            sendManaCountToServer(Math.min((int)(chargedManaCount + mana), maxManaCount), Inventory.SLOT_OFFHAND);

        }

    }

    public static void sendManaCountToServer(int manaCount, int slotIndex) {
        ArsArmsNetworkHandler.CHANNEL.sendToServer(new RequestSyncChargedManaMessage(manaCount, slotIndex));
    }

    private static boolean isTargetItem(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof AmmoBoxItem;
    }


}