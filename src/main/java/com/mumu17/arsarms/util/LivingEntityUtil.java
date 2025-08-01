package com.mumu17.arsarms.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class LivingEntityUtil {
    public static Entity getLivingEntityFromUUID(Entity entity) {
        Level level = entity.getCommandSenderWorld();
        ResourceKey<Level> dimension = level.dimension();
        MinecraftServer server = level.getServer();
        if (server != null) {
            ServerLevel serverLevel = server.getLevel(dimension);
            if (serverLevel != null) {
                return serverLevel.getEntity(entity.getUUID());
            }
        }
        return entity;
    }
}
