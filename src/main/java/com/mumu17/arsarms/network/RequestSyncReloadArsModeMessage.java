package com.mumu17.arsarms.network;

import com.mumu17.arsarms.util.ArsArmsReloadArsModeActive;
import com.mumu17.arsarms.util.ArsArmsReloadArsModeCancel;
import com.mumu17.arscurios.util.ArsCuriosInventoryHelper;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.item.AmmoBoxItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestSyncReloadArsModeMessage {
    private final int gunSlot;
    private final String curiosSlot;
    private final int flag;

    public RequestSyncReloadArsModeMessage(int gs, String cs, int f) {
        this.gunSlot = gs;
        this.curiosSlot = cs;
        this.flag = f;
    }

    public static void encode(RequestSyncReloadArsModeMessage msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.gunSlot);
        buf.writeUtf(msg.curiosSlot);
        buf.writeInt(msg.flag);
    }

    public static RequestSyncReloadArsModeMessage decode(FriendlyByteBuf buf) {
        return new RequestSyncReloadArsModeMessage(buf.readInt(), buf.readUtf(), buf.readInt());
    }

    public static void handle(RequestSyncReloadArsModeMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = ctx.get().getSender();
            if (player != null) {
                var gunStack = player.getInventory().getItem(msg.gunSlot);
                var curiosStack = ArsCuriosInventoryHelper.getCuriosInventoryItem(player, msg.curiosSlot);
                if (!curiosStack.isEmpty() && !gunStack.isEmpty() && curiosStack.getItem() instanceof AmmoBoxItem && gunStack.getItem() instanceof IGun) {
                    if (msg.flag == 0) {
                        ArsArmsReloadArsModeActive.active(gunStack, curiosStack, player, true);
                    } else if (msg.flag == 1) {
                        ArsArmsReloadArsModeActive.active(gunStack, curiosStack, player, false);
                    } else if (msg.flag == 2) {
                        ArsArmsReloadArsModeCancel.remove(gunStack, player);
                    } else if (msg.flag == 3) {
                        ArsArmsReloadArsModeCancel.cancel(gunStack, curiosStack);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
