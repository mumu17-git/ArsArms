package com.mumu17.arsarms.mixin.tacz;

import com.llamalad7.mixinextras.sugar.Local;
import com.mumu17.arsarms.util.GunItemCooldown;
import com.mumu17.arsarms.util.ModernKineticGunItemAccess;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.builder.GunItemBuilder;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GunItemBuilder.class)
public class GunItemBuilderMixin {

    @Shadow(remap = false)
    private int ammoCount;

    @Inject(method = "forceBuild", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IGun;setBulletInBarrel(Lnet/minecraft/world/item/ItemStack;Z)V", shift = At.Shift.AFTER), remap = false)
    private void forceBuild(CallbackInfoReturnable<?> cir, @Local(name = "iGun") IGun iGun, @Local(name = "gun") ItemStack gun) {
        addGunTags(iGun, gun);
    }

    @Inject(method = "build", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IGun;setBulletInBarrel(Lnet/minecraft/world/item/ItemStack;Z)V", shift = At.Shift.AFTER), remap = false)
    private void build(CallbackInfoReturnable<ItemStack> cir, @Local(name = "iGun") IGun iGun, @Local(name = "gun") ItemStack gun) {
        addGunTags(iGun, gun);
    }

    @Unique
    private void addGunTags(IGun iGun, ItemStack gun) {
        ModernKineticGunItemAccess access0 = (ModernKineticGunItemAccess) iGun;
        access0.setReloadAmoData(gun, false);
        GunItemCooldown access1 = (GunItemCooldown) iGun;
        access1.setLastAmmoCount(gun, ammoCount);
        access1.setGunDamage(gun, 0.0F);
        access1.setLastTimestamp(gun, System.currentTimeMillis());
    }
}
