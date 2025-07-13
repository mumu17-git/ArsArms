package com.mumu17.arsarms.mixin.ars;

import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SpellBook.class)
public class SpellBookMixin {

    @ModifyArg(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"), index = 0, remap = false)
    private InteractionHand modifyGetItemInHandArgs(InteractionHand par1, @Local(name = "playerIn") Player playerIn) {
        if (playerIn.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof SpellBook && !(playerIn.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SpellBook)) {
            return InteractionHand.OFF_HAND;
        } else {
            return InteractionHand.MAIN_HAND;
        }
    }

}
