package com.mumu17.arsarms.register;

import com.mumu17.arsarms.ArsArms;
import com.mumu17.arsarms.network.ArsArmsNetworkHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = ArsArms.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ArsArmsModEventSubscriber {
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        ArsArmsNetworkHandler.registerMessages();
    }
}