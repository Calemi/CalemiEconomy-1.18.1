package com.tm.calemieconomy.packet;

import com.tm.calemieconomy.blockentity.BlockEntityBank;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketBankSyncTransactionAmount extends PacketBlockEntity {

    private long transactionAmount;

    public PacketBankSyncTransactionAmount() {}

    public PacketBankSyncTransactionAmount(BlockPos pos, long transactionAmount) {
        super(pos);
        this.transactionAmount = transactionAmount;
    }

    public PacketBankSyncTransactionAmount(FriendlyByteBuf buf) {
        super(buf);
        this.transactionAmount = buf.readLong();
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeLong(transactionAmount);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx, ServerPlayer player, BlockEntity blockEntity) {

        if (blockEntity instanceof BlockEntityBank bank) {
            bank.setTransactionAmount(transactionAmount);
            bank.markUpdated();
        }
    }
}
