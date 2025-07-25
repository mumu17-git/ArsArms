package com.mumu17.arsarms.mixin.tacz;

import com.mumu17.arsarms.ArsArms;
import com.mumu17.arsarms.util.ArsArmsAmmoUtil;
import com.mumu17.arsarms.util.GunItemNbt;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.ReloadState;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import com.tacz.guns.item.ModernKineticGunItem;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.function.Supplier;

@Mixin(ModernKineticGunItem.class)
public class ModernKineticGunItemMixin implements GunItemNbt{

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

        return (LivingEntity) Objects.requireNonNull(server.getLevel(dimension)).getEntity(tag.getUUID("OwnerUUID"));
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
    public void setGunDamage(ItemStack gunItem, float ammoCount) {
        CompoundTag tag = gunItem.getOrCreateTag();
        tag.putFloat(LAST_GUN_DAMAGE, ammoCount);
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

    // May not be needed.
    @Inject(method = "shoot", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/Optional;ofNullable(Ljava/lang/Object;)Ljava/util/Optional;"), remap = false)
    public void shoot(ShooterDataHolder dataHolder, ItemStack gunItem, Supplier<Float> pitch, Supplier<Float> yaw, LivingEntity shooter, CallbackInfo ci) {
        if (gunItem.getItem() instanceof IGun iGun) {
            boolean useInventoryAmmo = iGun.useInventoryAmmo(gunItem);
            if(shooter instanceof Player player) {
                CommonGunIndex index = TimelessAPI.getCommonGunIndex(iGun.getGunId(gunItem)).orElse(null);
                if (index != null) {
                    GunData gunData = index.getGunData();
                    if (gunData != null) {
                        int ammoCount = useInventoryAmmo ? ArsArmsAmmoUtil.handleInventoryAmmo(gunItem, player.getInventory()) + (iGun.hasBulletInBarrel(gunItem) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0) :
                                iGun.getCurrentAmmoCount(gunItem) + (iGun.hasBulletInBarrel(gunItem) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0);
                        ammoCount = Math.min(ammoCount, MAX_AMMO_COUNT);
                    }
                }
            }
        }
    }

    @ModifyArg(method = "lambda$tickReload$12", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/entity/ReloadState;setStateType(Lcom/tacz/guns/api/entity/ReloadState$StateType;)V"), remap = false)
    private static ReloadState.StateType tickReload(ReloadState.StateType stateType) {
        if (!stateType.isReloading()) {

        }
        return stateType;
    }
}
