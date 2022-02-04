package com.tm.calemieconomy.packet;

import com.tm.calemieconomy.api.ICurrencyHolder;
import com.tm.calemieconomy.menu.MenuBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncContainerCurrency {

    private int currency;

    public PacketSyncContainerCurrency() {}

    /**
     * Used to sync the color data of the Pencil.
     * @param currency The currency amount to sync to.
     */
    public PacketSyncContainerCurrency(int currency) {
        this.currency = currency;
    }

    public PacketSyncContainerCurrency(FriendlyByteBuf buf) {
        currency = buf.readInt();
    }

    public void toBytes (FriendlyByteBuf buf) {
        buf.writeInt(currency);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();

            if (player != null) {

                if (player.containerMenu != null && player.containerMenu instanceof MenuBase menu) {

                    if (menu.getBlockEntity() instanceof ICurrencyHolder currencyHolder) {
                        currencyHolder.setCurrency(currency);
                        player.containerMenu.broadcastChanges();
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
