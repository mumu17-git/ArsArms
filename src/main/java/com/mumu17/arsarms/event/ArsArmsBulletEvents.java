package com.mumu17.arsarms.event;

import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import com.mumu17.arsarms.ArsArms;
import com.mumu17.arsarms.ArsArmsConfig;
import com.mumu17.arsarms.util.GunTags;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunFireEvent;
import com.tacz.guns.api.event.server.AmmoHitBlockEvent;
import com.tacz.guns.api.item.IGun;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ArsArms.MODID)
public class ArsArmsBulletEvents {

    private static final List<UUID> castingPlayers = new ArrayList<>(List.of());

    @SubscribeEvent
    public static void onGunFire(GunFireEvent event) {
        if (event.getLogicalSide().isClient()) return;
        if (!(event.getShooter() instanceof Player player)) return;

        ItemStack gunStack = event.getGunItemStack();
        ReactiveCaster reactiveCaster = new ReactiveCaster(gunStack);

        if (!player.isCreative()) {
            int manaCost = reactiveCaster.getSpell().getCost();
            if (GunTags.getMana(gunStack) < manaCost) {
                return;
            }
            GunTags.addMana(gunStack, -manaCost);
        }
        castingPlayers.add(player.getUUID());
    }

    @SubscribeEvent
    public static void onEntityHurtPre(EntityHurtByGunEvent.Pre event) {
        if (!(event.getAttacker() instanceof Player player)) return;
        if (!(event.getHurtEntity() instanceof LivingEntity livingEntity)) return;
        if (!castingPlayers.contains(player.getUUID())) return;
        castingPlayers.remove(player.getUUID());

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof IGun)) return;
        castSpell(livingEntity, stack);
    }

    @SubscribeEvent
    public static void onAmmoHitBlock(AmmoHitBlockEvent event) {
        if (!(event.getAmmo().getOwner() instanceof Player player)) return;
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof IGun)) return;
        if (!castingPlayers.contains(player.getUUID())) return;
        castingPlayers.remove(player.getUUID());

        Vec3 hitPos = event.getHitResult().getLocation();

        ArmorStand dummy = new ArmorStand(EntityType.ARMOR_STAND, player.level());
        dummy.setPos(hitPos.x, hitPos.y, hitPos.z);
        dummy.setInvisible(true);
        dummy.setInvulnerable(true);
        dummy.setNoGravity(true);

        CompoundTag tag = new CompoundTag();
        tag.putBoolean("Marker", true);
        tag.putBoolean("Small", true);
        dummy.readAdditionalSaveData(tag);

        player.level().addFreshEntity(dummy);

        castSpell(dummy, stack);

        dummy.discard();
    }

    private static void castSpell(LivingEntity target, ItemStack stack) {
        if (stack != ItemStack.EMPTY && canCastSpell(stack)) {
            ReactiveCaster reactiveCaster = new ReactiveCaster(stack);
            reactiveCaster.castSpell(target.getCommandSenderWorld(), target, InteractionHand.MAIN_HAND, (Component) null);
        }
    }

    private static boolean canCastSpell(ItemStack s) {
        return s.getAllEnchantments().containsKey(EnchantmentRegistry.REACTIVE_ENCHANTMENT.get()) && (!ArsArmsConfig.COMMON.castChance.get() || (double) s.getEnchantmentLevel((Enchantment) EnchantmentRegistry.REACTIVE_ENCHANTMENT.get()) * (double) 0.25F >= Math.random() && (new ReactiveCaster(s)).getSpell().isValid());
    }
}
