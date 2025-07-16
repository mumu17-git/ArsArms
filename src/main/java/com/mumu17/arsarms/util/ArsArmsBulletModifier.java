package com.mumu17.arsarms.util;

import com.mumu17.arsarms.ArsArmsConfig;

public class ArsArmsBulletModifier {
    public static double getAmplifier(double gunDamage) {
        double amp = ArsArmsConfig.COMMON.damageAmplifier.get();
        double d = 0.0;
        float c = 1;
        while (d < gunDamage) {
            d += amp * c;
            c++;
        }
        return c - 1;
    }
}
