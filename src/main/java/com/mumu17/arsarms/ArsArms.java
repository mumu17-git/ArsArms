package com.mumu17.arsarms;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.IModBusEvent;
import org.slf4j.Logger;

@Mod(ArsArms.MODID)
public class ArsArms {

    public static final String MODID = "arsarms";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ArsArms() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ArsArmsConfig.COMMON_SPEC);
    }
}
