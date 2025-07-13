package com.mumu17.arsarms.network;

import com.mumu17.arsarms.ArsArms;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ArsArmsNetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ArsArms.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerMessages() {
        CHANNEL.registerMessage(0, RequestSyncChargedManaMessage.class, RequestSyncChargedManaMessage::encode, RequestSyncChargedManaMessage::decode, RequestSyncChargedManaMessage::handle);
    }
}