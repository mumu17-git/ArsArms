package com.mumu17.arsarms.mixin.tacz;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mumu17.armslib.util.GunItemNbt;
import com.mumu17.arsarms.ArsArms;
import com.mumu17.arsarms.ArsArmsConfig;
import com.mumu17.arsarms.util.ArsArmsAmmoBox;
import com.mumu17.arsarms.util.ArsArmsCuriosUtil;
import com.mumu17.arsarms.util.ArsArmsProjectileData;
import com.mumu17.arscurios.util.InteractionHandUtil;
import com.mumu17.castlib.util.CastLibTags;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.item.ModernKineticGunItem;
import com.tacz.guns.util.TacHitResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityKineticBullet.class)
public class EntityKineticBulletMixin {

    @Shadow(remap = false) private float explosionDamage;
    @Shadow(remap = false) private boolean explosion;

    @Unique
    public void ArsArms$castSpell(LivingEntity attacker) {
        if (attacker instanceof Player player) {
            InteractionHand interactionHand = ArsArmsProjectileData.getInteractionHandFromPlayer(player);
            ItemStack curiosItem = player.getItemInHand(interactionHand);
            if (curiosItem != ItemStack.EMPTY && ArsArms$canCastSpell(curiosItem)) {
                ReactiveCaster reactiveCaster = new ReactiveCaster(curiosItem);
                CompoundTag tag = attacker.getPersistentData();
                CastLibTags.saveCastModIDToTag(tag, ArsArms.MODID);
                reactiveCaster.castSpell(attacker.getCommandSenderWorld(), attacker, interactionHand, (Component) null);
                ArsArms$consumeManaFromAmmoBox(curiosItem);
            }
        }
    }

    @Unique
    public boolean ArsArms$canCastSpell(ItemStack s) {
        return (double)s.getEnchantmentLevel((Enchantment) EnchantmentRegistry.REACTIVE_ENCHANTMENT.get()) * (double)0.25F >= Math.random() && (new ReactiveCaster(s)).getSpell().isValid();
    }

    @Unique
    public void ArsArms$consumeManaFromAmmoBox(ItemStack curiosAmmoBox) {
        int chargedManaCount = ArsArmsAmmoBox.getChargedManaCount(curiosAmmoBox);
        ReactiveCaster casterData = new ReactiveCaster(curiosAmmoBox);
        Spell spell = casterData.getSpell();
        int cost = spell.getCost();
        // ArsArms.LOGGER.debug("Total Mana: {}, Cost: {}", chargedManaCount, cost);
        curiosAmmoBox.getOrCreateTag().putInt("Mana", chargedManaCount - cost);
    }

    @Inject(method = "onHitEntity", at = @At(value = "HEAD"), remap = false)
    public void onHitEntity(TacHitResult result, Vec3 startVec, Vec3 endVec, CallbackInfo ci) {
        EntityKineticBullet projectileEntity = ((EntityKineticBullet)(Object)this);
        LivingEntity attacker = projectileEntity.getOwner() instanceof LivingEntity livingEntity ? livingEntity : null;
        if (attacker instanceof Player player) {
            ItemStack mainhand = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (mainhand.getItem() instanceof IGun iGun) {
                GunItemNbt access = (GunItemNbt) iGun;
                boolean isArsMode = access.getIsArsMode(mainhand);
                if (isArsMode) {
                    ArsArmsProjectileData.setProjectileData(projectileEntity, result.getEntity(), null, ArsArmsCuriosUtil.getCuriosSlotFromGun(player, mainhand));
                }
            }
        }
    }

    @Inject(id = "onHitEntity", method = "onHitEntity", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/entity/EntityKineticBullet$MaybeMultipartEntity;core()Lnet/minecraft/world/entity/Entity;", ordinal = 2), remap = false)
    public void onHitEntity(TacHitResult result, Vec3 startVec, Vec3 endVec, CallbackInfo ci, @Local(name = "attacker") LivingEntity attacker) {
        if (attacker instanceof Player player) {
            ItemStack mainhand = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (mainhand.getItem() instanceof IGun iGun) {
                GunItemNbt access = (GunItemNbt) iGun;
                boolean isArsMode = access.getIsArsMode(mainhand);
                if (isArsMode) {
                    if (result.getEntity().isAlive()) {
                        ArsArms$castSpell(attacker);
                    }
                }
            }
        }
    }

    @Inject(method = "onHitEntity", at = @At(value = "FIELD", target = "Lcom/tacz/guns/entity/EntityKineticBullet;explosion:Z", shift = At.Shift.AFTER), remap = false)
    public void explosionDamage(TacHitResult result, Vec3 startVec, Vec3 endVec, CallbackInfo ci) {
        if (explosion) {
            EntityKineticBullet projectileEntity = ((EntityKineticBullet)(Object)this);
            LivingEntity attacker = projectileEntity.getOwner() instanceof LivingEntity livingEntity ? livingEntity : null;
            if (attacker instanceof Player player) {
                ItemStack gunItem = player.getItemInHand(InteractionHand.MAIN_HAND);
                ArsArmsProjectileData arsArmsProjectileData = ArsArmsProjectileData.getProjectileData(projectileEntity);
                if (gunItem.getItem() instanceof IGun iGun && arsArmsProjectileData != null) {
                    Entity entity = arsArmsProjectileData.getTargetEntity();
                    InteractionHand hand = arsArmsProjectileData.getHand();
                    if (InteractionHandUtil.isAmmoBox(hand)) {
                        if (entity != null) {
                            GunItemNbt access = (GunItemNbt) iGun;
                            boolean isArsMode = access.getIsArsMode(gunItem);
                            if (isArsMode) {
                                explosionDamage *= ArsArmsConfig.COMMON.damageMultiplier.get();
                            }
                        }
                    }
                }
            }
        }
    }

    @ModifyReturnValue(method = "getDamage", at = @At(value = "RETURN"), remap = false)
    public float getDamage(float original) {
        EntityKineticBullet projectileEntity = ((EntityKineticBullet)(Object)this);
        ArsArmsProjectileData arsArmsProjectileData = ArsArmsProjectileData.getProjectileData(projectileEntity);
        if(arsArmsProjectileData != null && arsArmsProjectileData.isEnabled()) {
            Entity entity = arsArmsProjectileData.getTargetEntity();
            InteractionHand hand = arsArmsProjectileData.getHand();
            if (InteractionHandUtil.isAmmoBox(hand)) {
                if (entity != null) {
                    if (projectileEntity.getOwner() instanceof Player player) {
                        GunItemNbt gunItemCooldown = (GunItemNbt) player.getMainHandItem().getItem();
                        gunItemCooldown.setGunDamage(player.getMainHandItem(), original);
                        return (float) (original * ArsArmsConfig.COMMON.damageMultiplier.get());
                    }
                }
            }
        }
        return original;
    }

    @Inject(method = "onHitBlock", at = @At(value = "HEAD"), remap = false)
    public void onHitBlock_HEAD(BlockHitResult result, Vec3 startVec, Vec3 endVec, CallbackInfo ci) {
        EntityKineticBullet projectileEntity = ((EntityKineticBullet)(Object)this);
        LivingEntity attacker = projectileEntity.getOwner() instanceof LivingEntity livingEntity ? livingEntity : null;
        if (attacker instanceof Player player) {
            ItemStack mainhand = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (mainhand.getItem() instanceof IGun iGun) {
                GunItemNbt access = (GunItemNbt) iGun;
                boolean isArsMode = access.getIsArsMode(mainhand);
                if (isArsMode) {
                    ArsArmsProjectileData.setProjectileData(projectileEntity, null, result,ArsArmsCuriosUtil.getCuriosSlotFromGun(player, mainhand));
                }
            }
        }
    }

    @Inject(method = "onHitBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;onHitBlock(Lnet/minecraft/world/phys/BlockHitResult;)V"))
    public void onHitBlock(BlockHitResult result, Vec3 startVec, Vec3 endVec, CallbackInfo ci) {
        LivingEntity attacker = ((EntityKineticBullet)(Object)this).getOwner() instanceof LivingEntity livingEntity ? livingEntity : null;
        if (attacker instanceof Player player) {
            ItemStack mainhand = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (mainhand.getItem() instanceof IGun iGun) {
                GunItemNbt access = (GunItemNbt) iGun;
                boolean isArsMode = access.getIsArsMode(mainhand);
                if (isArsMode) {
                    if (result != null) {
                        ArsArms$castSpell(attacker);
                    }
                }
            }
        }
    }
}
