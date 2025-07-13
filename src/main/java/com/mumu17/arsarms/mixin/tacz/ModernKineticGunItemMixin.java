package com.mumu17.arsarms.mixin.tacz;

import com.hollingsworth.arsnouveau.common.enchantment.ReactiveEnchantment;
import com.mumu17.arsarms.util.ArsArmsReloadAmmoData;
import com.mumu17.arsarms.util.GunItemCooldown;
import com.mumu17.arsarms.util.ModernKineticGunItemAccess;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import com.tacz.guns.item.ModernKineticGunItem;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(ModernKineticGunItem.class)
public class ModernKineticGunItemMixin implements ModernKineticGunItemAccess, GunItemCooldown {

    @Unique
    private static final int MAX_AMMO_COUNT = 9999;

    @Unique
    @Override
    public void setReloadAmoData(ItemStack stack, boolean isArsMode) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean("ArsArmsIsArsMode", isArsMode);
    }

    @Unique
    @Override
    public ArsArmsReloadAmmoData getReloadAmoData(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("ArsArmsIsArsMode")) {
            boolean isArsMode = tag.getBoolean("ArsArmsIsArsMode");
            return new ArsArmsReloadAmmoData(isArsMode);
        }
        return null;
    }

    @Unique
    @Override
    public void setLastTimestamp(ItemStack gunItem, long timestamp) {
        CompoundTag tag = gunItem.getOrCreateTag();
        tag.putLong("LastShootTimestamp", timestamp);
    }

    @Unique
    @Override
    public long getLastTimestamp(ItemStack gunItem) {
        CompoundTag tag = gunItem.getTag();
        if (tag != null && tag.contains("LastShootTimestamp")) {
            return tag.getLong("LastShootTimestamp");
        }
        return 0L;
    }


    @Inject(method = "shoot", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/Optional;ofNullable(Ljava/lang/Object;)Ljava/util/Optional;"), remap = false)
    public void shoot(ShooterDataHolder dataHolder, ItemStack gunItem, Supplier<Float> pitch, Supplier<Float> yaw, LivingEntity shooter, CallbackInfo ci) {
        if (gunItem.getItem() instanceof ModernKineticGunItem modernKineticGunItem) {
            boolean useInventoryAmmo = modernKineticGunItem.useInventoryAmmo(gunItem);
            if(shooter instanceof Player player) {
                CommonGunIndex index = TimelessAPI.getCommonGunIndex(modernKineticGunItem.getGunId(gunItem)).orElse(null);
                if (index != null) {
                    GunData gunData = index.getGunData();
                    if (gunData != null) {
                        if (gunItem.getItem() instanceof IGun iGun) {
                            int ammoCount = useInventoryAmmo ? handleInventoryAmmo(gunItem, player.getInventory()) + (iGun.hasBulletInBarrel(gunItem) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0) :
                            iGun.getCurrentAmmoCount(gunItem) + (iGun.hasBulletInBarrel(gunItem) && gunData.getBolt() != Bolt.OPEN_BOLT ? 1 : 0);
                            ammoCount = Math.min(ammoCount, MAX_AMMO_COUNT);
                            if (ammoCount <= 1) {
                                ModernKineticGunItemAccess access = (ModernKineticGunItemAccess) iGun;
                                ArsArmsReloadAmmoData reloadAmmoData = access.getReloadAmoData(gunItem);
                                if (reloadAmmoData != null) {
                                    boolean isArsMode = reloadAmmoData.isArsMode();
                                    if (isArsMode) {
                                        if (gunItem.hasTag() && gunItem.getOrCreateTag().contains("ars_nouveau:reactive_caster")) {
                                            if (gunItem.getOrCreateTag().contains("Enchantments")) {
                                                ListTag enchantments = gunItem.getOrCreateTag().getList("Enchantments", Tag.TAG_COMPOUND);
                                                if (!enchantments.isEmpty()) {
                                                    for (int i = 0; i < enchantments.size(); i++) {
                                                        CompoundTag enchantmentTag = enchantments.getCompound(i);
                                                        String enchantmentId = enchantmentTag.getString("id");
                                                        if ("ars_nouveau:reactive".equals(enchantmentId)) {
                                                            enchantments.remove(i);
                                                            gunItem.getOrCreateTag().put("Enchantments", enchantments);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                            gunItem.getOrCreateTag().remove("ars_nouveau:reactive_caster");
                                        }
                                        access.setReloadAmoData(gunItem, false);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Unique
    private int handleInventoryAmmo(ItemStack stack, Inventory inventory) {
        int cacheInventoryAmmoCount = 0;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack inventoryItem = inventory.getItem(i);
            if (inventoryItem.getItem() instanceof IAmmo iAmmo && iAmmo.isAmmoOfGun(stack, inventoryItem)) {
                cacheInventoryAmmoCount += inventoryItem.getCount();
            }
            if (inventoryItem.getItem() instanceof IAmmoBox iAmmoBox && iAmmoBox.isAmmoBoxOfGun(stack, inventoryItem)) {
                if (iAmmoBox.isAllTypeCreative(inventoryItem) || iAmmoBox.isCreative(inventoryItem)) {
                    cacheInventoryAmmoCount = 9999;
                    return cacheInventoryAmmoCount;
                }
                cacheInventoryAmmoCount += iAmmoBox.getAmmoCount(inventoryItem);
            }
        }
        return cacheInventoryAmmoCount;
    }

}
