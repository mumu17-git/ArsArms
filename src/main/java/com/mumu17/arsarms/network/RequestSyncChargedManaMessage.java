package com.mumu17.arsarms.network;

import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.mumu17.arsarms.ArsArms;
import com.mumu17.arsarms.util.GunTags;
import com.tacz.guns.api.item.IGun;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestSyncChargedManaMessage {
    private final int manaCount;
    public static final String MANA = ArsArms.MODID+":Mana";
    public static final int MAX_MANA = 10000;

    public RequestSyncChargedManaMessage(int manaCount) {
        this.manaCount = manaCount;
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
                ItemStack stack = player.getMainHandItem();
                if (!stack.isEmpty() && stack.getItem() instanceof IGun) {
                    int chargedManaCount = GunTags.getMana(stack);
                    int removeManaCount = (int) ((float) msg.manaCount - (float) chargedManaCount);
                    if (removeManaCount > 0.0) {
                        CapabilityRegistry.getMana(player).ifPresent((mana) -> mana.removeMana(removeManaCount));
                    }
                    GunTags.addMana(stack, removeManaCount);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
