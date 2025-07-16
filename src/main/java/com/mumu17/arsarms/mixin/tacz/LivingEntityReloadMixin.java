package com.mumu17.arsarms.mixin.tacz;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mumu17.arsarms.util.*;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.entity.shooter.LivingEntityReload;
import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.item.ModernKineticGunItem;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.util.AttachmentDataUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityReload.class)
public class LivingEntityReloadMixin {

    @Shadow(remap = false)
    @Final private LivingEntity shooter;

    @ModifyExpressionValue(method = "lambda$reload$0", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/gun/AbstractGunItem;startReload(Lcom/tacz/guns/entity/shooter/ShooterDataHolder;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Z"), remap = false)
    private boolean onReload(boolean original, @Local(name = "gunItem") AbstractGunItem gunItem, @Local(name = "currentGunItem") ItemStack currentGunItem, @Local(name = "gunIndex") CommonGunIndex gunIndex) {
        if (!original) {
            return false;
        }
        if (shooter instanceof Player player) {
            ItemStack offhand = player.getItemInHand(InteractionHand.OFF_HAND);
            ModernKineticGunItemAccess access = (ModernKineticGunItemAccess) currentGunItem.getItem();
            ArsArmsReloadAmmoData reloadAmmoData = access.getReloadAmoData(currentGunItem);
            if (offhand.getItem() instanceof AmmoBoxItem ammoBoxItem) {
                boolean flag00 = false;

                if (offhand.hasTag() && offhand.getOrCreateTag().contains("ars_nouveau:reactive_caster")) {
                    Tag ammoBoxTag = offhand.getOrCreateTag().get("ars_nouveau:reactive_caster");
                    if (offhand.getOrCreateTag().contains("Enchantments")) {
                        ListTag enchantments = offhand.getOrCreateTag().getList("Enchantments", Tag.TAG_COMPOUND);
                        if (!enchantments.isEmpty()) {
                            for (int i = 0; i < enchantments.size(); i++) {
                                CompoundTag enchantmentTag = enchantments.getCompound(i);
                                String enchantmentId = enchantmentTag.getString("id");
                                if ("ars_nouveau:reactive".equals(enchantmentId)) {
                                    ListTag enchantmentsGunItem = new ListTag();
                                    if (currentGunItem.hasTag() && currentGunItem.getOrCreateTag().contains("Enchantments")) {
                                        enchantmentsGunItem = currentGunItem.getOrCreateTag().getList("Enchantments", Tag.TAG_COMPOUND);
                                        if (!enchantmentsGunItem.isEmpty()) {
                                            for (int j = 0; j < enchantmentsGunItem.size(); j++) {
                                                CompoundTag enchantmentTagGunItem = enchantmentsGunItem.getCompound(j);
                                                String enchantmentIdGunItem = enchantmentTagGunItem.getString("id");
                                                if ("ars_nouveau:reactive".equals(enchantmentIdGunItem)) {
                                                    enchantmentsGunItem.remove(j);
                                                    currentGunItem.getOrCreateTag().put("Enchantments", enchantmentsGunItem);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    enchantmentsGunItem.add(enchantmentTag);
                                    currentGunItem.getOrCreateTag().put("Enchantments", enchantmentsGunItem);
                                    flag00 = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (ammoBoxTag != null) {
                        currentGunItem.getOrCreateTag().put("ars_nouveau:reactive_caster", ammoBoxTag);
                    }

                    int chargedManaCount = offhand.getOrCreateTag().getInt("Mana");
                    ReactiveCaster casterData = new ReactiveCaster(offhand);
                    Spell spell = casterData.getSpell();
                    int cost = spell.getCost();
                    int reloadAmmoCount = 0;
                    if (currentGunItem.getItem() instanceof ModernKineticGunItem) {
                        int barrelBulletAmount = (gunItem.hasBulletInBarrel(currentGunItem) && gunIndex.getGunData().getBolt() != Bolt.OPEN_BOLT) ? 1 : 0;
                        reloadAmmoCount = AttachmentDataUtils.getAmmoCountWithAttachment(currentGunItem, gunIndex.getGunData()) + barrelBulletAmount;
                    }

                    offhand.getOrCreateTag().putInt("Mana", chargedManaCount - cost * reloadAmmoCount);

                    GunItemCooldown gunItemCooldown = (GunItemCooldown) gunItem;
                    gunItemCooldown.setLastAmmoCount(currentGunItem, reloadAmmoCount);

                    PlayerAmmoConsumer.setOffhand(shooter.getOffhandItem());
                }
                if (!flag00) {
                    access.setReloadAmoData(currentGunItem, false);
                } else {
                    access.setReloadAmoData(currentGunItem, true);
                }
            }
        }
        return true;
    }


}
