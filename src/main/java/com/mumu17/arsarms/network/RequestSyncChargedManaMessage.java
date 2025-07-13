package com.mumu17.arsarms.network;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.tacz.guns.item.AmmoBoxItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestSyncChargedManaMessage {
    private final int manaCount;

    public RequestSyncChargedManaMessage(int ManaCount) {
        this.manaCount = ManaCount;
    }

    public static void encode(RequestSyncChargedManaMessage msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.manaCount);
    }

    public static RequestSyncChargedManaMessage decode(FriendlyByteBuf buf) {
        return new RequestSyncChargedManaMessage(buf.readInt());
    }

    public static void handle(RequestSyncChargedManaMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = ctx.get().getSender();
            if (player != null) {
                var stack = player.getOffhandItem();
                if (!stack.isEmpty() && stack.getItem() instanceof AmmoBoxItem) {
                    int chargedManaCount = stack.getOrCreateTag().getInt("Mana");
                    CapabilityRegistry.getMana(player).ifPresent((mana) -> mana.removeMana((double) msg.manaCount - (double) chargedManaCount));
                    stack.getOrCreateTag().putInt("Mana", msg.manaCount);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
