package com.tm.calemieconomy.packet;

import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketTradingPostSyncExtremum extends PacketBlockEntity {

    private long extremum;

    public PacketTradingPostSyncExtremum() {}

    public PacketTradingPostSyncExtremum(BlockPos pos, long extremum) {
        super(pos);
        this.extremum = extremum;
    }

    public PacketTradingPostSyncExtremum(FriendlyByteBuf buf) {
        super(buf);
        this.extremum = buf.readLong();
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeLong(extremum);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx, ServerPlayer player, BlockEntity blockEntity) {

        if (blockEntity instanceof BlockEntityTradingPost post) {
            post.price.setExtremum(extremum);
            post.markUpdated();
        }
    }
}
