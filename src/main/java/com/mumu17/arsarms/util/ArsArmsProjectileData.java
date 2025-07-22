package com.mumu17.arsarms.util;

import com.mumu17.arscurios.util.ArsCuriosLivingEntity;
import com.mumu17.arscurios.util.ExtendedHand;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.item.ModernKineticGunItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class ArsArmsProjectileData {
    private final Entity targetEntity;
    private final BlockHitResult blockHitResult;
    private final InteractionHand hand;
    private final boolean isEnabled;
    private final ItemStack iGun;

    public ArsArmsProjectileData(Entity target, BlockHitResult block, InteractionHand h, ItemStack gun, boolean enabled) {
        targetEntity = target;
        blockHitResult = block;
        hand = h;
        isEnabled = enabled;
        iGun = gun;
    }

    public Entity getTargetEntity() {
        return targetEntity;
    }

    public BlockHitResult getBlockHitResult() {
        return blockHitResult;
    }

    public InteractionHand getHand() {
        return hand;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public ItemStack getCurrentGun() {
        return iGun;
    }

    private static final String
            IS_ARS_MODE = "IsArsMode",
            ARS_ARMS_HIT_ENTITY_UUID = "ArsArmsHitEntityUUID", ARS_ARMS_HIT_ENTITY_DIMENSION = "ArsArmsHitEntityDimension",
            ARS_ARMS_PROJECTILE_ENTITY_UUID = "ArsArmsProjectileEntityUUID", ARS_ARMS_PROJECTILE_ENTITY_DIMENSION = "ArsArmsProjectileEntityDimension",
            ARS_ARMS_BLOCK_HIT_X = "ArsArmsBlockHitX", ARS_ARMS_BLOCK_HIT_Y = "ArsArmsBlockHitY", ARS_ARMS_BLOCK_HIT_Z = "ArsArmsBlockHitZ",
            ARS_ARMS_BLOCK_HIT_FACE = "ArsArmsBlockHitFace",
            ARS_ARMS_BLOCK_HIT_BLOCK_X = "ArsArmsBlockHitBlockX", ARS_ARMS_BLOCK_HIT_BLOCK_Y = "ArsArmsBlockHitBlockY", ARS_ARMS_BLOCK_HIT_BLOCK_Z = "ArsArmsBlockHitBlockZ",
            ARS_ARMS_BLOCK_HIT_IS_INSIDE = "ArsArmsBlockHitIsInside",
            ARS_ARMS_HAND = "ArsArmsHand";


    private static void ArsArms$SaveBlockHitResultToTag(CompoundTag tag, BlockHitResult hitResult) {
        if (hitResult == null) return;
        tag.putDouble(ARS_ARMS_BLOCK_HIT_X, hitResult.getLocation().x);
        tag.putDouble(ARS_ARMS_BLOCK_HIT_Y, hitResult.getLocation().y);
        tag.putDouble(ARS_ARMS_BLOCK_HIT_Z, hitResult.getLocation().z);
        tag.putInt(ARS_ARMS_BLOCK_HIT_FACE, hitResult.getDirection().get3DDataValue());
        tag.putInt(ARS_ARMS_BLOCK_HIT_BLOCK_X, hitResult.getBlockPos().getX());
        tag.putInt(ARS_ARMS_BLOCK_HIT_BLOCK_Y, hitResult.getBlockPos().getY());
        tag.putInt(ARS_ARMS_BLOCK_HIT_BLOCK_Z, hitResult.getBlockPos().getZ());
        tag.putBoolean(ARS_ARMS_BLOCK_HIT_IS_INSIDE, hitResult.isInside());
    }

    private static BlockHitResult ArsArms$LoadBlockHitResultFromTag(CompoundTag tag) {
        if (tag == null || !tag.contains(ARS_ARMS_BLOCK_HIT_X) || !tag.contains(ARS_ARMS_BLOCK_HIT_Y) || !tag.contains(ARS_ARMS_BLOCK_HIT_Z) || !tag.contains(ARS_ARMS_BLOCK_HIT_FACE)) {
            return null;
        }

        double hitX = tag.getDouble(ARS_ARMS_BLOCK_HIT_X);
        double hitY = tag.getDouble(ARS_ARMS_BLOCK_HIT_Y);
        double hitZ = tag.getDouble(ARS_ARMS_BLOCK_HIT_Z);
        int faceIndex = tag.getInt(ARS_ARMS_BLOCK_HIT_FACE);

        Direction face = Direction.from3DDataValue(faceIndex);
        BlockPos blockPos = new BlockPos(tag.getInt(ARS_ARMS_BLOCK_HIT_BLOCK_X), tag.getInt(ARS_ARMS_BLOCK_HIT_BLOCK_Y), tag.getInt(ARS_ARMS_BLOCK_HIT_BLOCK_Z));
        Vec3 hitVec = new Vec3(hitX, hitY, hitZ);
        boolean isInside = tag.getBoolean(ARS_ARMS_BLOCK_HIT_IS_INSIDE);

        return new BlockHitResult(hitVec, face, blockPos, isInside);
    }


    private static void ArsArms$SaveEntityToTag(CompoundTag tag, Entity entity, Level world, byte flag) {
        if (entity == null) return;
        String TAG_UUID = flag == 0 ? ARS_ARMS_HIT_ENTITY_UUID : ARS_ARMS_PROJECTILE_ENTITY_UUID;
        String TAG_DIMENSION = flag == 0 ? ARS_ARMS_HIT_ENTITY_DIMENSION : ARS_ARMS_PROJECTILE_ENTITY_DIMENSION;
        tag.putUUID(TAG_UUID, entity.getUUID());
        tag.putString(TAG_DIMENSION, world.dimension().location().toString());
    }

    private static Entity ArsArms$LoadEntityFromTag(CompoundTag tag, Entity entity, byte flag) {
        String TAG_UUID = flag == 0 ? ARS_ARMS_HIT_ENTITY_UUID : ARS_ARMS_PROJECTILE_ENTITY_UUID;
        String TAG_DIMENSION = flag == 0 ? ARS_ARMS_HIT_ENTITY_DIMENSION : ARS_ARMS_PROJECTILE_ENTITY_DIMENSION;
        if (tag == null || !tag.hasUUID(TAG_UUID) || !tag.contains(TAG_DIMENSION)) {
            return null;
        }
        String dimId = tag.getString(TAG_DIMENSION);
        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimId));
        MinecraftServer server = entity.getCommandSenderWorld().getServer();
        if (server == null)
            return null;
        if (server.getLevel(dimension) == null)
            return null;

        return Objects.requireNonNull(server.getLevel(dimension)).getEntity(tag.getUUID(TAG_UUID));
    }

    private static void ArsArms$SaveIsArsModeToTag(CompoundTag tag, boolean isArsMode) {
        tag.putBoolean(IS_ARS_MODE, isArsMode);
    }

    private static boolean ArsArms$LoadIsArsModeFromTag(CompoundTag tag) {
        if (tag == null || !tag.contains(IS_ARS_MODE)) {
            return false;
        }
        return tag.getBoolean(IS_ARS_MODE);
    }

    private static void ArsArms$SaveHandToTag(CompoundTag tag, ExtendedHand hand) {
        tag.putString(ARS_ARMS_HAND, hand.name());
    }

    private static ExtendedHand ArsArms$LoadHandFromTag(CompoundTag tag) {
        if (tag != null && tag.contains(ARS_ARMS_HAND)) {
            String handName = tag.getString(ARS_ARMS_HAND);
            if (!handName.isEmpty()) {
                return ExtendedHand.valueOf(handName);
            }
        }
        return ExtendedHand.MAIN_HAND;
    }


    public static void setProjectileEntityToPlayer(LivingEntity player, Entity projectileEntity) {
        CompoundTag tag = player.getPersistentData();
        ArsArms$SaveEntityToTag(tag, projectileEntity, player.level(), (byte) 1);
    }

    public static Entity getProjectileEntityFromPlayer(LivingEntity player) {
        CompoundTag tag = player.getPersistentData();
        return ArsArms$LoadEntityFromTag(tag, player, (byte) 1);
    }

    public static InteractionHand getInteractionHandToPlayer(LivingEntity player) {
        CompoundTag tag = player.getPersistentData();
        ExtendedHand hand = ArsArms$LoadHandFromTag(tag);
        if (hand.getVanillaHand().isPresent()) {
            return hand.getVanillaHand().get();
        }
        return InteractionHand.MAIN_HAND;
    }

    public static void setProjectileToEntity(Entity entity, Entity projectileEntity) {
        CompoundTag tag = entity.getPersistentData();
        ArsArms$SaveEntityToTag(tag, projectileEntity, entity.level(), (byte) 1);
    }

    public static Entity getProjectileFromEntity(Entity entity) {
        CompoundTag tag = entity.getPersistentData();
        return ArsArms$LoadEntityFromTag(tag, entity, (byte) 1);
    }
    
    public static void setProjectileData(Entity projectileEntity, Entity hitEntity, BlockHitResult blockHitResult, ExtendedHand extendedHand, boolean isArsMode) {
        if (projectileEntity instanceof Projectile projectile){
            setProjectileEntityToPlayer((LivingEntity) projectile.getOwner(), projectileEntity);
            ArsCuriosLivingEntity.setPlayerExtendedHand((LivingEntity) projectile.getOwner(), extendedHand);
        }
        CompoundTag tag = projectileEntity.getPersistentData();
        ArsArms$SaveEntityToTag(tag, hitEntity, projectileEntity.level(), (byte) 0);
        ArsArms$SaveBlockHitResultToTag(tag, blockHitResult);
        ArsArms$SaveIsArsModeToTag(tag, isArsMode);
    }

    
    public static ArsArmsProjectileData getProjectileData(Entity projectileEntity) {
        CompoundTag tag = projectileEntity.getPersistentData();
        Entity hitEntity = ArsArms$LoadEntityFromTag(tag, projectileEntity, (byte) 0);
        BlockHitResult blockHitResult = ArsArms$LoadBlockHitResultFromTag(tag);
        boolean isArsMode = ArsArms$LoadIsArsModeFromTag(tag);
        Entity shooter = ((EntityKineticBullet) projectileEntity).getOwner();
        if (shooter instanceof Player player) {
            ItemStack itemStack = shooter.getSlot(player.getInventory().selected).get();
            InteractionHand hand = getInteractionHandToPlayer(player);
            if ((hitEntity != null || blockHitResult != null) && itemStack.getItem() instanceof ModernKineticGunItem) {
                return new ArsArmsProjectileData(hitEntity, blockHitResult, hand, itemStack, isArsMode);
            }
        }
        return null;
    }
}

