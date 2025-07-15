package com.mumu17.arsarms;

import net.minecraftforge.common.ForgeConfigSpec;

public class ArsArmsConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        COMMON = new Common(builder);
        COMMON_SPEC = builder.build();
    }

    public static class Common {
        public final ForgeConfigSpec.DoubleValue damageAmplifier;
        public final ForgeConfigSpec.DoubleValue damageMultiplier;

        public Common(ForgeConfigSpec.Builder builder) {
            damageMultiplier = builder
                    .comment("Projectile damage amplifier for TaCZ Gun projectiles.\nThe higher the value, the greater the damage.")
                    .defineInRange("damageMultiplier", 0.0, 0.0, 1.0);
            damageAmplifier = builder
                    .comment("Magic damage amplifier for TaCZ Gun projectiles.\nThe higher the value, the less damage.")
                    .defineInRange("damageAmplifier", 15.0, 0.01, 100.0);
        }
    }
}
