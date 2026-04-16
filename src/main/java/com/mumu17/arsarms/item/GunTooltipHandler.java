package com.mumu17.arsarms.item;

import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import com.mumu17.arsarms.ArsArms;
import com.mumu17.arsarms.util.GunTags;
import com.tacz.guns.api.item.IGun;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = ArsArms.MODID)
public class GunTooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof IGun)) return;
        if (!stack.getAllEnchantments().containsKey(EnchantmentRegistry.REACTIVE_ENCHANTMENT.get())) return;

        ReactiveCaster reactiveCaster = new ReactiveCaster(stack);

        List<Component> tooltip = event.getToolTip();

        tooltip.add(Component.literal(""));

        tooltip.add(Component.translatable("tooltip."+ArsArms.MODID+".inscribed_title")
                .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));

        tooltip.add(Component.translatable("tooltip."+ArsArms.MODID+".mana")
                .append(Component.literal(": "))
                .append(Component.literal(String.valueOf(GunTags.getMana(stack))).withStyle(ChatFormatting.AQUA)));
    }
}
