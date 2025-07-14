package com.mumu17.arsarms.mixin.tacz;

import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import com.llamalad7.mixinextras.sugar.Local;
import com.mumu17.arsarms.ArsArms;
import com.mumu17.arsarms.ArsArmsConfig;
import com.mumu17.arsarms.util.ArsArmsReloadAmmoData;
import com.mumu17.arsarms.util.GunItemCooldown;
import com.mumu17.arsarms.util.ModernKineticGunItemAccess;
import com.mumu17.arsarms.util.PlayerAmmoConsumer;
import com.tacz.guns.api.entity.ShootResult;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.entity.shooter.LivingEntityShoot;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import com.tacz.guns.item.AmmoBoxItem;
import com.tacz.guns.item.ModernKineticGunItem;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(LivingEntityShoot.class)
public class LivingEntityShootMixin {
    @Shadow(remap = false) @Final private LivingEntity shooter;
    @Shadow(remap = false) @Final private ShooterDataHolder data;

    @Inject(method = "shoot", at = @At(value = "RETURN"), remap = false)
    public void shoot(Supplier<Float> pitch, Supplier<Float> yaw, long timestamp, CallbackInfoReturnable<ShootResult> cir) {
        if (this.data.currentGunItem != null) {
            ItemStack currentGunItem = this.data.currentGunItem.get();
            if (currentGunItem.getItem() instanceof AbstractGunItem gunItem) {
                if (gunItem.useInventoryAmmo(currentGunItem)) {
                    if (shooter instanceof Player player) {
                        ItemStack offhand = player.getItemInHand(InteractionHand.OFF_HAND);
                        ModernKineticGunItemAccess access = (ModernKineticGunItemAccess) currentGunItem.getItem();
                        ArsArmsReloadAmmoData reloadAmmoData = access.getReloadAmoData(currentGunItem);
                        if (offhand.getItem() instanceof AmmoBoxItem) {
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
                                SpellStats.Builder spellStatsBuilder = new SpellStats.Builder();
                                double amplification = ArsArmsConfig.COMMON.damageAmplifier.get() != 0.0 ? ArsArmsConfig.COMMON.damageAmplifier.get() : 1.0;

                                SpellStats spellStats = spellStatsBuilder.addDamageModifier((double) currentGunItem.getDamageValue() / amplification).build();

                                int cost = spell.getCost();

                                int reloadAmmoCount = 0;
                                if (currentGunItem.getItem() instanceof ModernKineticGunItem) {
                                    reloadAmmoCount = 1;
                                }

                                offhand.getOrCreateTag().putInt("Mana", chargedManaCount - cost * reloadAmmoCount);
                            }
                            if (!flag00) {
                                access.setReloadAmoData(currentGunItem, false);
                            } else {
                                access.setReloadAmoData(currentGunItem, true);
                            }
                        }
                    }
                }

                GunItemCooldown gunItemCooldown = (GunItemCooldown) this.data.currentGunItem.get().getItem();
                long nowTime = System.currentTimeMillis();
                gunItemCooldown.setLastTimestamp(this.data.currentGunItem.get(), nowTime);
            }
        }
    }


    @Inject(method = "consumeAmmoFromPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getCapability(Lnet/minecraftforge/common/capabilities/Capability;Lnet/minecraft/core/Direction;)Lnet/minecraftforge/common/util/LazyOptional;"), remap = false)
    public void consumeAmmoFromPlayer(CallbackInfo ci) {
        PlayerAmmoConsumer.set(shooter.getOffhandItem());
    }


}
