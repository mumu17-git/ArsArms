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
        public final ForgeConfigSpec.BooleanValue castChance;

        public Common(ForgeConfigSpec.Builder builder) {
            castChance = builder
                    .comment("Control spell activation depending on the Reactive Enchantment level")
                    .define("castChance", false);
        }
    }
}
