package com.mumu17.arsarms.mixin.ars_nouveau;

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantmentRecipe;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.item.AmmoBoxItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentRecipe.class)
public class EnchantmentRecipeMixin {
    @ModifyReturnValue(method = "doesReagentMatch(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)Z", at = @At("RETURN"), remap = false)
    private static boolean doesReagentMatch$fixNullPointerException(boolean original, @Local(argsOnly = true) ItemStack stack) {
        if (original) {
            if (stack != null && stack.getItem() instanceof AmmoBoxItem) {
                return !stack.getOrCreateTag().contains("ISB_Spells");
            }
        }
        return original;
    }
}
