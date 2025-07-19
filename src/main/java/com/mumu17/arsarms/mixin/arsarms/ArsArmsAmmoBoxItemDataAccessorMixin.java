package com.mumu17.arsarms.mixin.arsarms;

import com.mumu17.arsarms.util.ArsArmsAmmoBoxItemDataAccessor;
import com.mumu17.arsarms.util.PlayerAmmoConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArsArmsAmmoBoxItemDataAccessor.class)
public class ArsArmsAmmoBoxItemDataAccessorMixin {

    @Inject(method = "isAmmoBoxOfGun", at = @At("RETURN"), remap = false)
    private void isAmmoBoxOfGun(CallbackInfoReturnable<Boolean> cir) {

    }

}
