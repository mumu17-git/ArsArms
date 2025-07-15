package com.mumu17.arsarms.mixin.ars;

import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.mumu17.arsarms.ArsArmsConfig;
import com.mumu17.arsarms.util.ArsArmsProjectileData;
import com.mumu17.arsarms.util.GunItemCooldown;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractEffect.class)
public abstract class AbstractEffectMixin {

    @Shadow(remap = false)
    public ForgeConfigSpec.DoubleValue DAMAGE;

    @Inject(method = "<init>(Ljava/lang/String;Ljava/lang/String;)V", at = @At("TAIL"), remap = false)
    private void AbstractEffect(String tag, String description, CallbackInfo ci) {
        if (ArsArmsProjectileData.isEnabled()) {
            GunItemCooldown gunItemCooldown = (GunItemCooldown) ArsArmsProjectileData.getCurrentGun().getItem();
            DAMAGE.set(DAMAGE.get() * (gunItemCooldown.getGunDamage(ArsArmsProjectileData.getCurrentGun()) / ArsArmsConfig.COMMON.damageAmplifier.get().floatValue()));
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;)V", at = @At("TAIL"), remap = false)
    private void AbstractEffect(ResourceLocation tag, String description, CallbackInfo ci) {
        if (ArsArmsProjectileData.isEnabled()) {
            GunItemCooldown gunItemCooldown = (GunItemCooldown) ArsArmsProjectileData.getCurrentGun().getItem();
            DAMAGE.set(DAMAGE.get() * (gunItemCooldown.getGunDamage(ArsArmsProjectileData.getCurrentGun()) / ArsArmsConfig.COMMON.damageAmplifier.get().floatValue()));
        }
    }
}
