package com.mumu17.arsarms.register;

import com.mumu17.arsarms.ArsArms;
import com.mumu17.arsarms.util.PlayerInventoryListener;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsArms.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ArsArmsForgeEventSubscriber {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerInventoryListener.registerListener(event.getEntity());
    }
}
