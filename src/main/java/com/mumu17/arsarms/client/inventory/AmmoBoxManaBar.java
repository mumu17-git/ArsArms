package com.mumu17.arsarms.client.inventory;

import com.mumu17.arsarms.ArsArms;
import com.mumu17.arsarms.util.ArsArmsAmmoBox;
import com.tacz.guns.item.AmmoBoxItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsArms.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class AmmoBoxManaBar {

    @SubscribeEvent
    public static void onRenderForeground(ContainerScreenEvent.Render.Foreground event) {
        AbstractContainerScreen<?> screen = event.getContainerScreen();
        GuiGraphics graphics = event.getGuiGraphics();

        for (Slot slot : screen.getMenu().slots) {
            ItemStack stack = slot.getItem();
            if (!isAmmoBox(stack)) continue;

            int value = getManaValue(stack);
            int max = getManaMax(stack);
            if (max <= 0) continue;

            int barWidth = Math.round(13.0F * ((float) value / (float) max));
            int barHeight = 1;
            int x = slot.x + 2;
            int y = slot.y + 1;

            graphics.fill(x, y, x + 13, y + barHeight*2, 0xFF000000);

            graphics.fill(x, y, x + barWidth, y + barHeight, 0xFFAA00FF);
        }
    }

    public static boolean isAmmoBox(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof AmmoBoxItem;
    }

    public static int getManaValue(ItemStack stack) {
        return ArsArmsAmmoBox.getChargedManaCount(stack);
    }
    
    public static int getManaMax(ItemStack stack) {
        return ArsArmsAmmoBox.getMaxManaCount(stack);
    }

    
}
