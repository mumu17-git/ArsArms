package com.mumu17.arsarms.client;

import com.mumu17.arsarms.client.inventory.AmmoBoxManaBar;
import com.mumu17.arsarms.util.PlayerAmmoConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@OnlyIn(Dist.CLIENT)
public class AmmoBoxManaBarOverlay implements IGuiOverlay {

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = gui.getMinecraft();
        Screen screen = mc.screen;
        if (mc.player == null || mc.options.hideGui) return;
        Player player = mc.player;
        PlayerAmmoConsumer.setPlayer(mc.player);
        int hotbarY = screenHeight - 22;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!AmmoBoxManaBar.isAmmoBox(stack)) continue;

            int value = AmmoBoxManaBar.getManaValue(stack);
            int max = AmmoBoxManaBar.getManaMax(stack);
            if (max <= 0) continue;

            int barWidth = Math.round(13.0F * ((float) value / (float) max));
            int barHeight = 1;
            int x = screenWidth / 2 - 90 + i * 20 + 4;
            int y = hotbarY + 4;

            graphics.fill(x, y, x + 13, y + barHeight * 2, 0xFF000000);
            graphics.fill(x, y, x + barWidth, y + barHeight, 0xFFAA00FF);
        }

        ItemStack offhand = player.getOffhandItem();
        if (AmmoBoxManaBar.isAmmoBox(offhand)) {
            int value = AmmoBoxManaBar.getManaValue(offhand);
            int max = AmmoBoxManaBar.getManaMax(offhand);
            if (max > 0) {
                int barWidth = Math.round(13.0F * ((float) value / (float) max));
                int barHeight = 1;
                int x = screenWidth / 2 - 115;
                int y = hotbarY + 4;

                graphics.fill(x, y, x + 13, y + barHeight * 2, 0xFF000000);
                graphics.fill(x, y, x + barWidth, y + barHeight, 0xFFAA00FF);
            }
        }
        PlayerAmmoConsumer.clearPlayer();
    }
}
