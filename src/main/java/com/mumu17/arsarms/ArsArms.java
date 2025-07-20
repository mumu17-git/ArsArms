package com.mumu17.arsarms;

import com.mojang.logging.LogUtils;
import com.mumu17.arsarms.util.ArsArmsAmplifierProvider;
import com.mumu17.arsarms.util.ArsArmsGunCooldownProvider;
import com.mumu17.arsarms.util.ArsArmsProjectileProvider;
import com.mumu17.castlib.util.ProviderRegistry;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(ArsArms.MODID)
public class ArsArms {

    public static final String MODID = "arsarms";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ArsArms() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ArsArmsConfig.COMMON_SPEC);
        ProviderRegistry.registerProjectileProvider(new ArsArmsProjectileProvider());
        ProviderRegistry.registerGunItemCooldownProvider(new ArsArmsGunCooldownProvider());
        ProviderRegistry.registerDamageAmplifierProvider(new ArsArmsAmplifierProvider());

    }
}
