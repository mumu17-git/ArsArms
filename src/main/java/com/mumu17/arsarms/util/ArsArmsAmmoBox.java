package com.mumu17.arsarms.util;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.config.sync.SyncConfig;
import com.tacz.guns.item.AmmoBoxItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.atomic.AtomicInteger;

public class ArsArmsAmmoBox {

    public static int getMaxManaCount(ItemStack stack) {
        if (stack.getItem() instanceof AmmoBoxItem ammoBoxItem) {
            ResourceLocation boxAmmoId = ammoBoxItem.getAmmoId(stack);

            if (boxAmmoId.equals(DefaultAssets.EMPTY_AMMO_ID)) {
                if (stack.getOrCreateTag().contains("LastAmmoStackSize")) {
                    return stack.getOrCreateTag().getInt("LastAmmoStackSize");
                } else {
                    return 0;
                }
            }
            AtomicInteger maxSize = new AtomicInteger();
            TimelessAPI.getCommonAmmoIndex(boxAmmoId).ifPresent((index) -> {
                int boxLevelMultiplier = ammoBoxItem.getAmmoLevel(stack) + 1;
                PlayerAmmoConsumer.getPlayer().getOffhandItem().getOrCreateTag().putInt("LastAmmoStackSize", index.getStackSize());
                maxSize.set(index.getStackSize() * (Integer) SyncConfig.AMMO_BOX_STACK_SIZE.get() * boxLevelMultiplier);
            });

            ReactiveCaster casterData = new ReactiveCaster(stack);
            Spell spell = casterData.getSpell();
            int cost = spell.getCost();

            return cost * maxSize.get();
        }

        return 0;
    }

    public static int getChargedManaCount(ItemStack stack) {
        if (stack.getItem() instanceof AmmoBoxItem) {
            if (stack.hasTag() && stack.getOrCreateTag().contains("Mana")) {
                return stack.getOrCreateTag().getInt("Mana");
            }
        }
        return 0;
    }
}
