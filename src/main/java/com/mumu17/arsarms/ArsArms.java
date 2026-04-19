package com.mumu17.arsarms;

import com.mojang.logging.LogUtils;
import com.mumu17.arsarms.event.ArsArmsBulletEvents;
import com.mumu17.arsarms.register.ModNetworking;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.ModLoadingException;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(ArsArms.MODID)
public class ArsArms {

    public static final String MODID = "arsarms";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ArsArms() {
        if (ModList.get().isLoaded("arscurios")
                || ModList.get().isLoaded("armslib")
                || ModList.get().isLoaded("castlib")) {
            throw new ModLoadingException(
                    ModLoadingContext.get().getActiveContainer().getModInfo(),
                    ModLoadingStage.CONSTRUCT,
                    "This mod is incompatible with §eArsCurios, ArmsLib and CastLib§r. Please remove them.",
                    new Throwable()
            );
        }

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ArsArmsConfig.COMMON_SPEC);
        MinecraftForge.EVENT_BUS.register(ArsArmsBulletEvents.class);
        ModNetworking.register();
    }
}
