package com.tm.calemieconomy.packet;

import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketTradingPostSyncAmount extends PacketBlockEntity {

    private int amount;

    public PacketTradingPostSyncAmount() {}

    public PacketTradingPostSyncAmount(BlockPos pos, int amount) {
        super(pos);
        this.amount = amount;
    }

    public PacketTradingPostSyncAmount(FriendlyByteBuf buf) {
        super(buf);
        this.amount = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(amount);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx, ServerPlayer player, BlockEntity blockEntity) {

        if (blockEntity instanceof BlockEntityTradingPost post) {
            post.tradeAmount = amount;
            post.markUpdated();
        }
    }
}
