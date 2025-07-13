package com.mumu17.arsarms.mixin.ars;

import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.mumu17.arsarms.util.ArsArmsProjectileData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MethodTouch.class)
public class MethodTouchMixin {

    @ModifyVariable(method = "onCastOnEntity", at = @At(value = "HEAD"), ordinal = 0, remap = false, argsOnly = true)
    public Entity onCastOnEntity_entity(Entity original) {
        if (ArsArmsProjectileData.isEnabled()) {
            Entity entity = ArsArmsProjectileData.getTargetEntity();
            InteractionHand hand = ArsArmsProjectileData.getHand();
            if (hand == InteractionHand.OFF_HAND) {
                if (entity != null) {
                    return entity;
                }
            }
        }
        return original;
    }

    @ModifyVariable(method = "onCastOnEntity", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true, remap = false)
    public InteractionHand onCastOnEntity_hand(InteractionHand original) {
        if (ArsArmsProjectileData.isEnabled()) {
            Entity entity = ArsArmsProjectileData.getTargetEntity();
            InteractionHand hand = ArsArmsProjectileData.getHand();
            if (hand == InteractionHand.OFF_HAND) {
                if (entity != null) {
                    return hand;
                }
            }
        }
        return original;
    }

    @ModifyVariable(method = "onCastOnBlock(Lnet/minecraft/world/item/context/UseOnContext;Lcom/hollingsworth/arsnouveau/api/spell/SpellStats;Lcom/hollingsworth/arsnouveau/api/spell/SpellContext;Lcom/hollingsworth/arsnouveau/api/spell/SpellResolver;)Lcom/hollingsworth/arsnouveau/api/spell/CastResolveType;", at = @At(value = "HEAD"), ordinal = 0, remap = false, argsOnly = true)
    public UseOnContext onCastOnEntity_UseOnContext(UseOnContext value) {
        if (ArsArmsProjectileData.isEnabled()) {
            BlockHitResult blockHR = ArsArmsProjectileData.getBlockHitResult();
            InteractionHand hand = ArsArmsProjectileData.getHand();
            if (hand == InteractionHand.OFF_HAND) {
                if (blockHR != null && value.getPlayer() != null) {
                    return new UseOnContext(value.getPlayer(), hand, blockHR);
                }
            }
        }
        return value;
    }

    @ModifyVariable(method = "onCastOnBlock(Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/entity/LivingEntity;Lcom/hollingsworth/arsnouveau/api/spell/SpellStats;Lcom/hollingsworth/arsnouveau/api/spell/SpellContext;Lcom/hollingsworth/arsnouveau/api/spell/SpellResolver;)Lcom/hollingsworth/arsnouveau/api/spell/CastResolveType;", at = @At(value = "HEAD"), ordinal = 0, remap = false, argsOnly = true)
    public BlockHitResult onCastOnBlock_BlockHitResult(BlockHitResult original) {
        if (ArsArmsProjectileData.isEnabled()) {
            BlockHitResult blockHR = ArsArmsProjectileData.getBlockHitResult();
            InteractionHand hand = ArsArmsProjectileData.getHand();
            if (hand == InteractionHand.OFF_HAND) {
                if (blockHR != null) {
                    return blockHR;
                }
            }
        }
        return original;
    }

}
