package com.mumu17.arsarms.util;

import com.mumu17.castlib.util.IDamageAmplifierProvider;

public class ArsArmsAmplifierProvider implements IDamageAmplifierProvider {

    @Override
    public double getAmplifier(double gunDamage) {
        return ArsArmsBulletModifier.getAmplifier(gunDamage);
    }
}
