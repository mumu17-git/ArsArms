package com.mumu17.arsarms.register;

import com.mumu17.arsarms.ArsArms;
import com.mumu17.arsarms.network.RequestSyncChargedManaMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworking {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ArsArms.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(id++, RequestSyncChargedManaMessage.class, RequestSyncChargedManaMessage::encode, RequestSyncChargedManaMessage::decode, RequestSyncChargedManaMessage::handle);
//        INSTANCE.registerMessage(id++, RequestSyncReloadArsModeMessage.class, RequestSyncReloadArsModeMessage::encode, RequestSyncReloadArsModeMessage::decode, RequestSyncReloadArsModeMessage::handle);
    }
}