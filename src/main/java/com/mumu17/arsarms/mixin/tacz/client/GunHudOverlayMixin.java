package com.mumu17.arsarms.mixin.tacz.client;

import com.mumu17.arsarms.util.ArsArmsAmmoUtil;
import com.tacz.guns.client.gui.overlay.GunHudOverlay;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GunHudOverlay.class)
public class GunHudOverlayMixin {

    @Shadow(remap = false)
    private static int cacheInventoryAmmoCount = 0;

    /**
     * @author mumu17
     * @reason Redirect the method to use ArsArmsAmmoUtil for handling inventory ammo count.
     */
    @Overwrite(remap = false)
    private static void handleInventoryAmmo(ItemStack stack, Inventory inventory) {
        cacheInventoryAmmoCount = ArsArmsAmmoUtil.handleInventoryAmmo(stack, inventory);
    }
}
