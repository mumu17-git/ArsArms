package com.mumu17.arsarms.mixin.tacz;

import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mumu17.arsarms.ArsArmsConfig;
import com.mumu17.arsarms.util.ArsArmsReloadAmmoData;
import com.mumu17.arsarms.util.GunItemNbt;
import com.mumu17.arsarms.util.ModernKineticGunItemAccess;
import com.mumu17.arscurios.util.ArsCuriosLivingEntity;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.item.ModernKineticGunItem;
import com.tacz.guns.util.TacHitResult;
import com.mumu17.arsarms.util.ArsArmsProjectileData;
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
    public void ArsArms$castSpell(LivingEntity playerIn, ItemStack s) {
        if (ArsArms$canCastSpell(s)) {
            ReactiveCaster reactiveCaster = new ReactiveCaster(s);
            reactiveCaster.castSpell(playerIn.getCommandSenderWorld(), playerIn, InteractionHand.OFF_HAND, (Component)null);
        }
    }

    @Unique
    public boolean ArsArms$canCastSpell(ItemStack s) {
        return (double)s.getEnchantmentLevel((Enchantment) EnchantmentRegistry.REACTIVE_ENCHANTMENT.get()) * (double)0.25F >= Math.random() && (new ReactiveCaster(s)).getSpell().isValid();
    }

    @Inject(method = "onHitEntity", at = @At(value = "HEAD"), remap = false)
    public void onHitEntity(TacHitResult result, Vec3 startVec, Vec3 endVec, CallbackInfo ci) {
        EntityKineticBullet projectileEntity = ((EntityKineticBullet)(Object)this);
        LivingEntity attacker = projectileEntity.getOwner() instanceof LivingEntity livingEntity ? livingEntity : null;
        if (attacker instanceof Player player) {
            ItemStack mainhand = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (mainhand.getItem() instanceof ModernKineticGunItem modernKineticGunItem && mainhand.hasTag() && mainhand.getOrCreateTag().contains("ars_nouveau:reactive_caster")) {
                ModernKineticGunItemAccess access = (ModernKineticGunItemAccess) modernKineticGunItem;
                ArsArmsReloadAmmoData reloadAmmoData = access.getReloadAmoData(mainhand);
                ArsArmsProjectileData.setProjectileData((Entity) projectileEntity, result.getEntity(), null, ArsCuriosLivingEntity.getPlayerExtendedHand(player), reloadAmmoData.isArsMode());
            }
        }
    }

    @Inject(id = "onHitEntity", method = "onHitEntity", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/entity/EntityKineticBullet$MaybeMultipartEntity;core()Lnet/minecraft/world/entity/Entity;", ordinal = 2), remap = false)
    public void onHitEntity(TacHitResult result, Vec3 startVec, Vec3 endVec, CallbackInfo ci, @Local(name = "attacker") LivingEntity attacker) {
        if (attacker != null) {
            Player player = (Player) attacker;
            ItemStack mainhand = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (mainhand.getItem() instanceof ModernKineticGunItem && mainhand.hasTag() && mainhand.getOrCreateTag().contains("ars_nouveau:reactive_caster")) {
                if(result.getEntity().isAlive()) {
                    ArsArms$castSpell(player, mainhand);
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
                if (gunItem.getItem() instanceof ModernKineticGunItem modernKineticGunItem && arsArmsProjectileData != null) {
                    Entity entity = arsArmsProjectileData.getTargetEntity();
                    InteractionHand hand = arsArmsProjectileData.getHand();
                    if (hand == InteractionHand.OFF_HAND) {
                        if (entity != null) {
                            ModernKineticGunItemAccess access = (ModernKineticGunItemAccess) modernKineticGunItem;
                            ArsArmsReloadAmmoData reloadAmmoData = access.getReloadAmoData(gunItem);
                            if (reloadAmmoData != null) {
                                boolean isArsMode = reloadAmmoData.isArsMode();
                                if (isArsMode) {
                                    explosionDamage *= ArsArmsConfig.COMMON.damageMultiplier.get();
                                }
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
            if (hand == InteractionHand.OFF_HAND) {
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
            if (mainhand.getItem() instanceof ModernKineticGunItem modernKineticGunItem && mainhand.hasTag() && mainhand.getOrCreateTag().contains("ars_nouveau:reactive_caster")) {
                ModernKineticGunItemAccess access = (ModernKineticGunItemAccess) modernKineticGunItem;
                ArsArmsReloadAmmoData reloadAmmoData = access.getReloadAmoData(mainhand);
                ArsArmsProjectileData.setProjectileData(projectileEntity, null, result, ArsCuriosLivingEntity.getPlayerExtendedHand(player), reloadAmmoData.isArsMode());
            }
        }
    }

    @Inject(method = "onHitBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;onHitBlock(Lnet/minecraft/world/phys/BlockHitResult;)V"))
    public void onHitBlock(BlockHitResult result, Vec3 startVec, Vec3 endVec, CallbackInfo ci) {
        LivingEntity attacker = ((EntityKineticBullet)(Object)this).getOwner() instanceof LivingEntity livingEntity ? livingEntity : null;
        if (attacker instanceof Player player) {
            ItemStack mainhand = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (mainhand.getItem() instanceof ModernKineticGunItem && mainhand.hasTag() && mainhand.getOrCreateTag().contains("ars_nouveau:reactive_caster")) {
                if (result != null) {
                    ArsArms$castSpell(player, mainhand);
                }
            }
        }
    }
}
