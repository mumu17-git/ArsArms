package com.mumu17.arsarms.mixin.ars;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mumu17.arsarms.util.ArsArmsProjectileData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SpellResolver.class)
public class SpellResolverMixin {

    @ModifyExpressionValue(method = "onCastOnEntity", at = @At(value = "INVOKE", target = "Lcom/hollingsworth/arsnouveau/api/spell/SpellResolver;canCast(Lnet/minecraft/world/entity/LivingEntity;)Z"), remap = false)
    public boolean onCastOnEntity(boolean original) {
        return original;
    }

    @ModifyVariable(method = "expendMana", at = @At("STORE"), ordinal = 0, remap = false)
    public int expendMana_totalCost(int original) {
        if (ArsArmsProjectileData.isEnabled()) {
            return 0;
        }
        return original;
    }

    @ModifyVariable(method = "enoughMana", at = @At("STORE"), ordinal = 0, remap = false)
    public int enoughMana_totalCost(int original) {
        if (ArsArmsProjectileData.isEnabled()) {
            return 0;
        }
        return original;
    }
}
