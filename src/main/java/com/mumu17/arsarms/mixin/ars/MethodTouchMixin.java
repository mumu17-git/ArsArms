package com.mumu17.arsarms.mixin.ars;

import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.SpellPartConfigUtil;
import com.mumu17.arsarms.ArsArmsConfig;
import com.mumu17.arsarms.util.ArsArmsBulletModifier;
import com.mumu17.arsarms.util.ArsArmsProjectileData;
import com.mumu17.arsarms.util.GunItemCooldown;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

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

    @ModifyVariable(method = "onCastOnEntity", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true, remap = false)
    public SpellResolver onCastOnEntity_spellResolver(SpellResolver original) {
        if (ArsArmsProjectileData.isEnabled()) {
            Entity entity = ArsArmsProjectileData.getTargetEntity();
            InteractionHand hand = ArsArmsProjectileData.getHand();
            GunItemCooldown gunItemCooldown = (GunItemCooldown) ArsArmsProjectileData.getCurrentGun().getItem();
            float damageMultiply = (float) ArsArmsBulletModifier.getAmplifier((gunItemCooldown.getGunDamage(ArsArmsProjectileData.getCurrentGun())));
            if (hand == InteractionHand.OFF_HAND) {
                if (entity != null) {
                    List<AbstractSpellPart> modified_parts = new ArrayList<>(List.copyOf(original.spell.recipe));
                    for (AbstractSpellPart part : original.spell.recipe) {
                        if (!(part.getGlyph().spellPart instanceof AbstractAugment)) {
                            modified_parts.add(part);
                            /*for (int i = 0; i < damageMultiply;i++) {
                                modified_parts.add(GlyphRegistry.getSpellPart(new ResourceLocation("ars_nouveau", GlyphLib.AugmentAmplifyID)));
                            }*/
                        } else {
                            modified_parts.add(part);
                        }
                    }
                    for (int i = 0; i < damageMultiply;i++) {
                        modified_parts.add(GlyphRegistry.getSpellPart(new ResourceLocation("ars_nouveau", GlyphLib.AugmentAmplifyID)));
                    }
                    original.spell.setRecipe(modified_parts);
                    return original;
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

    @ModifyVariable(method = "onCastOnBlock(Lnet/minecraft/world/item/context/UseOnContext;Lcom/hollingsworth/arsnouveau/api/spell/SpellStats;Lcom/hollingsworth/arsnouveau/api/spell/SpellContext;Lcom/hollingsworth/arsnouveau/api/spell/SpellResolver;)Lcom/hollingsworth/arsnouveau/api/spell/CastResolveType;", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true, remap = false)
    public SpellResolver onCastOnBlock_spellResolver(SpellResolver original) {
        if (ArsArmsProjectileData.isEnabled()) {
            BlockHitResult blockHR = ArsArmsProjectileData.getBlockHitResult();
            InteractionHand hand = ArsArmsProjectileData.getHand();
            GunItemCooldown gunItemCooldown = (GunItemCooldown) ArsArmsProjectileData.getCurrentGun().getItem();
            float damageMultiply =  (float) ArsArmsBulletModifier.getAmplifier(gunItemCooldown.getGunDamage(ArsArmsProjectileData.getCurrentGun()));
            if (hand == InteractionHand.OFF_HAND) {
                if (blockHR != null) {
                    List<AbstractSpellPart> modified_parts = new ArrayList<>(List.copyOf(original.spell.recipe));
                    for (AbstractSpellPart part : original.spell.recipe) {
                        if (!(part.getGlyph().spellPart instanceof AbstractAugment)) {
                            modified_parts.add(part);
                            /*for (int i = 0; i < damageMultiply;i++) {
                                modified_parts.add(GlyphRegistry.getSpellPart(new ResourceLocation("ars_nouveau", GlyphLib.AugmentAmplifyID)));
                            }*/
                        } else {
                            modified_parts.add(part);
                        }
                    }
                    for (int i = 0; i < damageMultiply;i++) {
                        modified_parts.add(GlyphRegistry.getSpellPart(new ResourceLocation("ars_nouveau", GlyphLib.AugmentAmplifyID)));
                    }
                    original.spell.setRecipe(modified_parts);
                    return original;
                }
            }
        }
        return original;
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
