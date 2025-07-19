package com.mumu17.arsarms.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerInventoryListener {
    public static void registerListener(Player player) {
        player.inventoryMenu.addSlotListener(new ContainerListener() {
            @Override
            public void slotChanged(@NotNull AbstractContainerMenu menu, int slotIndex, @NotNull ItemStack currentGunItem) {

            }

            @Override
            public void dataChanged(@NotNull AbstractContainerMenu p_150524_, int p_150525_, int p_150526_) {

            }
        });
    }
}
