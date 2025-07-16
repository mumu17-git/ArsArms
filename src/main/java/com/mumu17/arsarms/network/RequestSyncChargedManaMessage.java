package com.mumu17.arsarms.network;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.mumu17.arsarms.util.PlayerAmmoConsumer;
import com.tacz.guns.item.AmmoBoxItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestSyncChargedManaMessage {
    private final int manaCount;
    private final int ammoBox;

    public RequestSyncChargedManaMessage(int ManaCount, int stack) {
        this.manaCount = ManaCount;
        this.ammoBox = stack;
    }

    public static void encode(RequestSyncChargedManaMessage msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.manaCount);
        buf.writeInt(msg.ammoBox);
    }

    public static RequestSyncChargedManaMessage decode(FriendlyByteBuf buf) {
        return new RequestSyncChargedManaMessage(buf.readInt(), buf.readInt());
    }

    public static void handle(RequestSyncChargedManaMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = ctx.get().getSender();
            if (player != null) {
                var stack = player.getInventory().getItem(msg.ammoBox);
                if (!stack.isEmpty() && stack.getItem() instanceof AmmoBoxItem) {
                    int chargedManaCount = stack.getOrCreateTag().getInt("Mana");
                    double removeManaCount = ((double) msg.manaCount - (double) chargedManaCount);
                    if (removeManaCount > 0.0) {
                        CapabilityRegistry.getMana(player).ifPresent((mana) -> mana.removeMana(removeManaCount));
                    }
                    stack.getOrCreateTag().putInt("Mana", msg.manaCount);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
