package com.mumu17.arsarms.mixin;

import com.mumu17.arsarms.util.ArsArmsAmmoBox;
import com.tacz.guns.item.AmmoBoxItem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getBarWidth()I"))
    public void renderItemDecorations$afterBarVisible(Font font, ItemStack itemStack, int slotX, int slotY, String countLabel, CallbackInfo ci) {
        if (ArsArms$isAmmoBox(itemStack)) {
            int value = ArsArmsAmmoBox.getChargedManaCount(itemStack);
            int max = ArsArmsAmmoBox.getMaxManaCount(itemStack);
            if (max > 0) {
                float barWidthFactor = (float) value / (float) max;
                if (barWidthFactor > 1.0F) {
                    barWidthFactor = 1.0F;
                } else if (barWidthFactor < 0.0F) {
                    barWidthFactor = 0.0F;
                }
                int barWidth = Math.round(13.0F * barWidthFactor);
                int barHeight = 1;
                int x = slotX + 2;
                int y = slotY + 1;

                GuiGraphics graphics = ((GuiGraphics)(Object)this);

                graphics.fill(x, y, x + 13, y + barHeight * 2, 0xFF000000);
                graphics.fill(x, y, x + barWidth, y + barHeight, 0xFFAA00FF);
            }
        }
    }

    @Unique
    private boolean ArsArms$isAmmoBox(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof AmmoBoxItem;
    }
}
