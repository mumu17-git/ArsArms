package com.mumu17.arsarms.client;

import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.mumu17.arsarms.ArsArms;
import com.mumu17.arsarms.network.ArsArmsNetworkHandler;
import com.mumu17.arsarms.network.RequestSyncChargedManaMessage;
import com.mumu17.arsarms.util.ArsArmsAmmoBox;
import com.mumu17.arsarms.util.PlayerAmmoConsumer;
import com.tacz.guns.item.AmmoBoxItem;
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

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            PlayerAmmoConsumer.setPlayer(mc.player);
            ItemStack offhand = mc.player.getOffhandItem();
            if (isTargetItem(offhand)) {
                chargeManaOrCancel(mc.player);
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