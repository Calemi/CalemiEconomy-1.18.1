package com.tm.calemieconomy.packet;

import com.tm.calemieconomy.util.helper.CurrencyHelper;
import com.tm.calemieconomy.menu.MenuWallet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class PacketOpenWallet {

    public PacketOpenWallet() {}

    public PacketOpenWallet(FriendlyByteBuf buf) {}

    public void toBytes (FriendlyByteBuf buf) {}

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();

            if (player != null) {

                ItemStack stack = CurrencyHelper.getCurrentWallet(player);

                NetworkHooks.openGui(player, new SimpleMenuProvider((id, playerInventory, unused) -> {
                    return new MenuWallet(id, playerInventory, stack);
                }, new TranslatableComponent("container.wallet")), buffer -> {
                    buffer.writeItem(stack);
                });
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
