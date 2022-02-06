package com.tm.calemieconomy.packet;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.menu.MenuBase;
import com.tm.calemieconomy.blockentity.BlockEntityBank;
import com.tm.calemieconomy.item.ItemWallet;
import com.tm.calemieconomy.util.IBlockCurrencyHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketBank {

    private int bankCurrency;
    private int walletCurrency;
    private BlockPos pos;

    public PacketBank () {}

    /**
     * Handles syncing data to the Bank on the server.
     * @param bankCurrency The Bank's stored currency.
     * @param walletCurrency The Bank's Wallet's stored currency.
     * @param pos The Block position of the Bank.
     */
    public PacketBank (int bankCurrency, int walletCurrency, BlockPos pos) {
        this.bankCurrency = bankCurrency;
        this.walletCurrency = walletCurrency;
        this.pos = pos;
    }

    public PacketBank (FriendlyByteBuf buf) {
        this.bankCurrency = buf.readInt();
        this.walletCurrency = buf.readInt();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public void toBytes (FriendlyByteBuf buf) {
        buf.writeInt(bankCurrency);
        buf.writeInt(walletCurrency);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();

            if (player != null) {

                Location location = new Location(player.getLevel(), pos);

                //Checks if the Tile Entity is a Bank.
                if (location.getBlockEntity() != null && location.getBlockEntity() instanceof BlockEntityBank bank) {

                    if (bank.getItem(1).getItem() instanceof ItemWallet wallet) {
                        bank.setCurrency(bankCurrency);
                        wallet.setCurrency(bank.getItem(1), walletCurrency);
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
