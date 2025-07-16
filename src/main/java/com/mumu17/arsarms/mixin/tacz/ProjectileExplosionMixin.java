package com.mumu17.arsarms.mixin.tacz;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.mumu17.arsarms.util.ArsArmsProjectileData;
import com.mumu17.arsarms.util.ArsArmsReloadAmmoData;
import com.mumu17.arsarms.util.ModernKineticGunItemAccess;
import com.tacz.guns.item.ModernKineticGunItem;
import com.tacz.guns.util.block.ProjectileExplosion;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileExplosion.class)
public class ProjectileExplosionMixin {

    @Shadow(remap = false) @Final
    private Entity owner;

    @Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    public void setDamage(CallbackInfo ci, @Local(name = "damage") LocalDoubleRef damage) {
        if (owner instanceof Player player && player.getMainHandItem().getItem() instanceof ModernKineticGunItem modernKineticGunItem) {
            ModernKineticGunItemAccess access = (ModernKineticGunItemAccess) modernKineticGunItem;
            ArsArmsReloadAmmoData reloadAmmoData = access.getReloadAmoData(player.getMainHandItem());
            if (reloadAmmoData != null) {
                boolean isArsMode = reloadAmmoData.isArsMode();
                if (isArsMode) {
                    damage.set(0.0);
                }
            }
        }
    }

}
