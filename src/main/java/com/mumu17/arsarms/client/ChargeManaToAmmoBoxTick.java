package com.mumu17.arsarms.client;

import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.mumu17.arsarms.ArsArms;
import com.mumu17.arsarms.network.ArsArmsNetworkHandler;
import com.mumu17.arsarms.network.RequestSyncChargedManaMessage;
import com.mumu17.arsarms.util.ArsArmsAmmoBox;
import com.mumu17.arscurios.util.ArsCuriosInventoryHelper;
import com.mumu17.arscurios.util.ExtendedHand;
import com.tacz.guns.item.AmmoBoxItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsArms.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ChargeManaToAmmoBoxTick {
    private static final int MAX_AMMO_COUNT = 9999;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            for (ExtendedHand hand : ExtendedHand.values()) {
                ItemStack stack = ArsCuriosInventoryHelper.getCuriosInventoryItem(mc.player, hand.getSlotName());
                if (isTargetItem(stack)) {
                    chargeManaOrCancel(mc.player, stack, hand.getSlotName());
                }
            }
        }
    }

    private static void chargeManaOrCancel(LocalPlayer player, ItemStack stack, String curiosSlot) {
        if (isTargetItem(stack)) {
            int chargedManaCount = ArsArmsAmmoBox.getChargedManaCount(stack);
            if (chargedManaCount < 0) {
                chargedManaCount = 0;
            }

            double mana = ManaUtil.getCurrentMana(player);
            if (mana < 100) {
                return;
            }

            int maxManaCount = ArsArmsAmmoBox.getMaxManaCount(stack);

            int maxChargedManaCount = ArsArmsAmmoBox.getMaxChargedManaCount(stack);

            if (chargedManaCount >= maxChargedManaCount) {
                return;
            }
            
            sendManaCountToServer(Math.min((int)(chargedManaCount + mana), maxManaCount), curiosSlot);
        }
    }

    public static void sendManaCountToServer(int manaCount, String curiosSlot) {
        ArsArmsNetworkHandler.CHANNEL.sendToServer(new RequestSyncChargedManaMessage(manaCount, curiosSlot));
    }

    private static boolean isTargetItem(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof AmmoBoxItem;
    }


}