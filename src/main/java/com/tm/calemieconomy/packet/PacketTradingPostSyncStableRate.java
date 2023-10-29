package com.tm.calemieconomy.packet;

import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketTradingPostSyncStableRate extends PacketBlockEntity {

    private float stableRate;

    public PacketTradingPostSyncStableRate() {}

    public PacketTradingPostSyncStableRate(BlockPos pos, float stableRate) {
        super(pos);
        this.stableRate = stableRate;
    }

    public PacketTradingPostSyncStableRate(FriendlyByteBuf buf) {
        super(buf);
        this.stableRate = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeFloat(stableRate);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx, ServerPlayer player, BlockEntity blockEntity) {

        if (blockEntity instanceof BlockEntityTradingPost post) {
            post.price.stableRate = stableRate;
            post.markUpdated();
        }
    }
}
