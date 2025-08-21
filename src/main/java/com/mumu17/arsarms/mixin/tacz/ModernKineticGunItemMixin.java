package com.mumu17.arsarms.mixin.tacz;

import com.mumu17.armslib.util.GunItemNbt;
import com.tacz.guns.item.ModernKineticGunItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;

@Mixin(ModernKineticGunItem.class)
public class ModernKineticGunItemMixin implements GunItemNbt {

    @Unique
    private static final int MAX_AMMO_COUNT = 9999;

    @Unique
    private static final String
            IS_ARS_MODE = "ArsArmsIsArsMode", OWNER_UUID = "OwnerUUID", OWNER_DIMENSION = "OwnerDimension",
            LAST_SHOOT_TIMESTAMP = "LastShootTimestamp", LAST_AMMO_COUNT = "LastAmmoCount", LAST_GUN_DAMAGE = "LastGunDamage";

    @Unique
    private void ArsArms$SaveIsArsModeToGunTag(CompoundTag tag, boolean isArsMode) {
        tag.putBoolean(IS_ARS_MODE, isArsMode);
    }

    @Unique
    private boolean ArsArms$LoadIsArsModeFromGunTag(CompoundTag tag) {
        if (tag == null || !tag.contains(IS_ARS_MODE)) {
            return false;
        }
        return tag.getBoolean(IS_ARS_MODE);
    }

    @Unique
    @Override
    public void setIsArsMode(ItemStack stack, boolean isArsMode) {
        CompoundTag tag = stack.getOrCreateTag();
        ArsArms$SaveIsArsModeToGunTag(tag, isArsMode);
    }

    @Unique
    @Override
    public boolean getIsArsMode(ItemStack gun) {
        CompoundTag tag = gun.getTag();
        return ArsArms$LoadIsArsModeFromGunTag(tag);
    }

    @Unique
    @Override
    public void setOwner(ItemStack gunItem, LivingEntity owner) {
        CompoundTag tag = gunItem.getOrCreateTag();
        if (owner != null) {
            tag.putUUID(OWNER_UUID, owner.getUUID());
            tag.putString(OWNER_DIMENSION, owner.level().dimension().location().toString());
        }
    }

    @Unique
    @Override
    public LivingEntity getOwner(ItemStack gunItem) {
        CompoundTag tag = gunItem.getOrCreateTag();
        if (!tag.hasUUID(OWNER_UUID) || !tag.contains(OWNER_DIMENSION)) {
            return null;
        }
        String dimId = tag.getString(OWNER_DIMENSION);
        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimId));
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null)
            return null;
        if (server.getLevel(dimension) == null)
            return null;

        return (LivingEntity) Objects.requireNonNull(server.getLevel(dimension)).getEntity(tag.getUUID(OWNER_UUID));
    }

    @Unique
    @Override
    public void setLastTimestamp(ItemStack gunItem, long timestamp) {
        CompoundTag tag = gunItem.getOrCreateTag();
        tag.putLong(LAST_SHOOT_TIMESTAMP, timestamp);
    }

    @Unique
    @Override
    public long getLastTimestamp(ItemStack gunItem) {
        CompoundTag tag = gunItem.getTag();
        if (tag != null && tag.contains(LAST_SHOOT_TIMESTAMP)) {
            return tag.getLong(LAST_SHOOT_TIMESTAMP);
        }
        return 0L;
    }

    @Unique
    @Override
    public void setLastAmmoCount(ItemStack gunItem, int ammoCount) {
        CompoundTag tag = gunItem.getOrCreateTag();
        tag.putInt(LAST_AMMO_COUNT, ammoCount);
    }

    @Unique
    @Override
    public int getLastAmmoCount(ItemStack gunItem) {
        CompoundTag tag = gunItem.getTag();
        if (tag != null && tag.contains(LAST_AMMO_COUNT)) {
            return tag.getInt(LAST_AMMO_COUNT);
        }
        return 0;
    }

    @Unique
    @Override
    public void setGunDamage(ItemStack gunItem, float damage) {
        CompoundTag tag = gunItem.getOrCreateTag();
        tag.putFloat(LAST_GUN_DAMAGE, damage);
    }

    @Unique
    @Override
    public float getGunDamage(ItemStack gunItem) {
        CompoundTag tag = gunItem.getTag();
        if (tag != null && tag.contains(LAST_GUN_DAMAGE)) {
            return tag.getFloat(LAST_GUN_DAMAGE);
        }
        return 0.0F;
    }
}
