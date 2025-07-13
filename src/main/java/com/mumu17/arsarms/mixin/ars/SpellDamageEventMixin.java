package com.mumu17.arsarms.mixin.ars;

import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.mumu17.arsarms.util.ArsArmsProjectileData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpellDamageEvent.class)
public class SpellDamageEventMixin {

    @Shadow(remap = false)
    public LivingEntity caster;

    @Shadow(remap = false) public float damage;

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void onSpellDamageEvent(DamageSource source, LivingEntity shooter, Entity entity, float totalDamage, SpellContext context, CallbackInfo ci) {
        if (ArsArmsProjectileData.isEnabled()) {
            damage = totalDamage * ArsArmsProjectileData.getDamageMultiplier();
        }
    }
}
